package capstone.relation.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import capstone.relation.meeting.service.MeetRoomService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

	private final SocketRegistry socketRegistry;
	private final MeetRoomService meetRoomService;

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String socketId = headerAccessor.getUser().getName();
		Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
		System.out.println("User Disconnected : " + userId);
		if (userId == null) {
			return;
		}
		// meetRoomService.leaveRoom(userId);
		System.out.println("register SocketId" + socketRegistry.getSocketId(userId.toString()));
		if (socketId == socketRegistry.getSocketId(userId.toString()))
			socketRegistry.unregisterSession(userId.toString());
	}
}
