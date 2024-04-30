package capstone.relation.websocket;

import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class StompPreHandler implements ChannelInterceptor {
	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
			String authToken = accessor.getFirstNativeHeader("Authorization");
			if (authToken == null) {
				throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
			}
			Authentication authentication = tokenProvider.getAuthentication(authToken);
			if (authentication != null) {
				setUserDetails(accessor, authentication);
				return message;
			}
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		return message;
	}

	private void setUserDetails(StompHeaderAccessor accessor, Authentication authentication) {
		Long userId = (Long)authentication.getPrincipal();
		String userName = userRepository.findById(userId).get().getUserName();
		setSessionAttributes(accessor, userId, userName);
		log.info("Session attributes after setting: " + Objects.requireNonNull(accessor.getSessionAttributes())
			.toString());
	}

	private void setSessionAttributes(StompHeaderAccessor accessor, Long userId,
		String userName) {
		Map<String, Object> sessionAttributes = Objects.requireNonNull(accessor.getSessionAttributes());
		sessionAttributes.put("userName", userName);
		sessionAttributes.put("userId", userId);
		accessor.setSessionAttributes(sessionAttributes);
	}
}

