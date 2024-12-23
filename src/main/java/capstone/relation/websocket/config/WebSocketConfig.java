package capstone.relation.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import capstone.relation.websocket.chat.stomp.StompErrorHandler;
import capstone.relation.websocket.chat.stomp.StompHandshakeHandler;
import capstone.relation.websocket.chat.stomp.StompPreHandler;
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
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.setErrorHandler(stompErrorHandler)
			.addEndpoint("/ws-chat")
			.setAllowedOriginPatterns("*")
			.setHandshakeHandler(customHandshakeHandler)
			.withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompPreHandler);
	}
}

