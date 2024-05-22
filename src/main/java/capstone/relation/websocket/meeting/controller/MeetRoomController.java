package capstone.relation.websocket.meeting.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.service.MeetRoomService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetRoomController {

	private final MeetRoomService meetRoomService;

	@MessageMapping("/room/create")
	public void createRoom(CreateRoomDto createRoomDto,
		SimpMessageHeaderAccessor headerAccessor) throws Exception {
		meetRoomService.createRoom(createRoomDto, headerAccessor);
	}

	@MessageMapping("/room/join/{roomId}")
	public void joinRoom(@DestinationVariable Long roomId,
		SimpMessageHeaderAccessor headerAccessor) {
		System.out.println("joinRoom");
		System.out.println("roomId: " + roomId);
		if (roomId == null) {
			return;
		}
		try {
			meetRoomService.joinRoom(headerAccessor.getSessionAttributes(), roomId);
		} catch (Exception e) {
			meetRoomService.sendErrorMessage(headerAccessor, e.getMessage(), "/queue/join", 400);
		}
	}

	@MessageMapping("/room/list")
	public void requestRoomList(SimpMessageHeaderAccessor headerAccessor) {
		String workSpaceId = (String)headerAccessor.getSessionAttributes().get("workSpaceId");
		meetRoomService.sendRoomList(workSpaceId);
	}

	@MessageMapping("/room/leave")
	public void leaveRoom(SimpMessageHeaderAccessor headerAccessor) {
		meetRoomService.leaveRoom(headerAccessor.getSessionAttributes());
	}

}
