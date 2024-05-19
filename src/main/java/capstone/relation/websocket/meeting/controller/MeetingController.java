package capstone.relation.websocket.meeting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetingController {

	private final MeetingService meetingService;

	@MessageMapping("/room/create")
	public void createRoom(CreateRoomDto createRoomDto,
		SimpMessageHeaderAccessor headerAccessor) throws Exception {
		meetingService.createRoom(createRoomDto, headerAccessor);
	}

	@MessageMapping("/app/room/join/{roomId}")
	public ResponseEntity<?> joinRoom(@DestinationVariable String roomId,
		SimpMessageHeaderAccessor headerAccessor) {
		String workSpaceId = (String)headerAccessor.getSessionAttributes().get("workSpaceId");
		// JoinResponseDto joinResponseDto = meetingService.joinRoom(headerAccessor.getSessionAttributes(), roomId);
		return ResponseEntity.ok().build();
	}

	@MessageMapping("/leave/{roomId}")
	public ResponseEntity<?> leaveRoom() {
		return ResponseEntity.ok().build();
	}

}
