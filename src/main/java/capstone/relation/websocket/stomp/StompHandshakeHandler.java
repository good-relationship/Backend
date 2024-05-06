package capstone.relation.websocket.stomp;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request,
		WebSocketHandler wsHandler,
		Map<String, Object> attributes) {
		System.out.println("HANDSHAKE");
		return new StompPrincipal(UUID.randomUUID().toString());
	}
}
