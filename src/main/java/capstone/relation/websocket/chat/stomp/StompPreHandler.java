package capstone.relation.websocket.chat.stomp;

import java.time.Instant;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.workspace.WorkSpace;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class StompPreHandler implements ChannelInterceptor {
	private final TokenProvider tokenProvider;
	private final ThreadPoolTaskScheduler taskScheduler;
	private final SocketRegistry socketRegistry;
	private final UserRepository userRepository;

	StompPreHandler(TokenProvider tokenProvider, SocketRegistry socketRegistry,
		UserRepository userRepository) {
		this.tokenProvider = tokenProvider;
		taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();
		this.socketRegistry = socketRegistry;
		this.userRepository = userRepository;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
			System.out.println("preSend CONNECT가 실행");
			String token = accessor.getFirstNativeHeader("Authorization");
			System.out.println("token: " + token);
			if (token == null) {
				throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
			}
			if (!tokenProvider.validateToken(token)) {
				throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
			}
			Long userId = tokenProvider.getUserId(token);
			Long expiryTime = tokenProvider.getExpiryFromToken(token); //나중에 만료시간 설정해서 스캐줄러에 넣으려고
			socketRegistry.registerSession(userId.toString(), accessor.getUser().getName());
			accessor.getSessionAttributes().put("userId", userId);
			accessor.getSessionAttributes().put("expiryTime", expiryTime);
			userRepository.findById(userId).ifPresent(user -> {
				WorkSpace workSpace = user.getWorkSpace();
				if (workSpace != null) {
					accessor.getSessionAttributes().put("workSpaceId", workSpace.getId());
				} else {
					throw new AuthException(AuthErrorCode.INVALID_WORKSPACE_STATE_USER);
				}
			});
			System.out.println("워크 스페이스 아이디: " + accessor.getSessionAttributes().get("workSpaceId"));
			scheduleSessionExpiry(accessor, expiryTime);
		}
		return message;
	}

	private void scheduleSessionExpiry(StompHeaderAccessor accessor, Long expiryTime) {
		Instant expireAt = Instant.ofEpochMilli(expiryTime); // 밀리초 단위의 타임스탬프를 Instant 객체로 변환
		System.out.println("Session expiry scheduled for sessionId: " + accessor + " at: " + expireAt);
		taskScheduler.schedule(() -> {
			accessor.getSessionAttributes().clear();
			System.out.println("Session expired and automatically closed for sessionId: 세션 만료!!");
		}, expireAt);
	}
}

