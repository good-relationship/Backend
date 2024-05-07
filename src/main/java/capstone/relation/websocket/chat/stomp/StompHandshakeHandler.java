<<<<<<<< HEAD:src/main/java/capstone/relation/websocket/chat/StompHandshakeHandler.java
package capstone.relation.websocket.chat;
========
package capstone.relation.websocket.stomp;
>>>>>>>> 8323bac8933232be621dd5549580fa8fe90bf58f:src/main/java/capstone/relation/websocket/chat/stomp/StompHandshakeHandler.java

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
