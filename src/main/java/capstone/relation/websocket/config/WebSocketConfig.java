package capstone.relation.websocket.stomp;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final StompPreHandler stompPreHandler;
	private final StompErrorHandler stompErrorHandler;
	private final StompHandshakeHandler customHandshakeHandler;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// 메시지를 구독하는 요청 url
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.setErrorHandler(stompErrorHandler)
			.addEndpoint("/ws-chat")
			.setAllowedOriginPatterns("*")
			.setHandshakeHandler(customHandshakeHandler);
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompPreHandler);
	}
}

