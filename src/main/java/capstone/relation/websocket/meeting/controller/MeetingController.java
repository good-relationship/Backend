package capstone.relation.websocket.meeting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.dto.response.JoinResponseDto;
import capstone.relation.websocket.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.websocket.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetingController {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final SocketRegistry socketRegistry;
	private final MeetingService meetingService;

	@MessageMapping("/room/create")
	public void createRoom(CreateRoomDto createRoomDto,
		SimpMessageHeaderAccessor headerAccessor) throws Exception {
		String roomName = createRoomDto.getRoomName();
		String socketId = socketRegistry.getSocketId((String)headerAccessor.getSessionAttributes().get("userId"));
		if (roomName == null || roomName.isEmpty()) {
			simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/join",
				ResponseEntity.status(401).body("요청 인수가 올바르지 않습니다."));
		}
		try {
			String workSpaceId = (String)headerAccessor.getSessionAttributes().get("workSpaceId");
			JoinResponseDto joinResponseDto = meetingService.createRoom(headerAccessor.getSessionAttributes(),
				roomName);
			MeetingRoomListDto roomList = meetingService.getRoomList(workSpaceId);
			simpMessagingTemplate.convertAndSend("/topic/" + workSpaceId + "/meetingRoomList", roomList);
			simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/join", joinResponseDto);
		} catch (AuthException e) {
			simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/join",
				ResponseEntity.status(401).body("Access token is already expired or invalid."));
		} catch (Exception e) {
			simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/join",
				ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다."));
		}
	}

	@MessageMapping("/app/room/join/{roomId}")
	public ResponseEntity<?> joinRoom() {
		return ResponseEntity.ok().build();
	}

	@MessageMapping("/leaveRoom")
	public ResponseEntity<?> leaveRoom() {
		return ResponseEntity.ok().build();
	}

}
