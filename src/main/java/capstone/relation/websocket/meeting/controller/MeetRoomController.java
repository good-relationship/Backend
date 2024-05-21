package capstone.relation.websocket.meeting.controller;

import static org.apache.commons.validator.GenericValidator.*;

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
	public void joinRoom(@DestinationVariable String roomId,
		SimpMessageHeaderAccessor headerAccessor) {
		if (roomId == null || roomId.isEmpty()) {
			return;
		}
		if (isLong(roomId)) {
			try {
				meetRoomService.joinRoom(headerAccessor.getSessionAttributes(), Long.parseLong(roomId));
			} catch (Exception e) {
				meetRoomService.sendErrorMessage(headerAccessor, e.getMessage(), "/queue/join", 400);
			}
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
