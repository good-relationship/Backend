package capstone.relation.websocket.meeting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.dto.response.JoinResponseDto;
import capstone.relation.websocket.meeting.service.MeetRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Meet-Room", description = "미팅 방 생성 관련 API(HTTP)")
@RestController
@RequestMapping("/meet/room")
@RequiredArgsConstructor
public class MeetRoomController {
	private final MeetRoomService meetRoomService;
	private final UserService userService;

	@PostMapping("/create")
	@Operation(summary = "회의방 생성", description =
		"새로운 회의방을 생성합니다. 생성된 회의방은 `/topic/{workSpaceId}/meetingRoomList`로 생성된 방에 대한 목록 발송이 이루어집니다.\n"
			+ "방 생성자는 자동으로 방에 참여합니다.\n"
	)
	public JoinResponseDto createRoom(@RequestBody CreateRoomDto createRoomDto) throws Exception {
		User user = userService.getUserEntity();
		return meetRoomService.createAndJoinRoom(createRoomDto, user.getId(), user.getWorkSpace().getId());
	}

	@PostMapping("/join/{roomId}")
	@Operation(summary = "회의방 참여", description = "회의방에 참여합니다.")
	public JoinResponseDto joinRoom(@RequestParam Long roomId) {
		User user = userService.getUserEntity();
		return meetRoomService.joinRoom(user.getId(), user.getWorkSpace().getId(), roomId);
	}

	@GetMapping("/list")
	@Operation(summary = "회의방 목록 요청", description = "현재 워크스페이스에 있는 회의방 목록을 요청합니다.\n"
		+ "이것에 대한 응답은 `/topic/{workSpaceId}/meetingRoomList`로 이루어집니다.\n"
	)
	public void requestRoomList() {
		User user = userService.getUserEntity();
		meetRoomService.sendRoomList(user.getWorkSpace().getId());
	}

	@PostMapping("/leave")
	@Operation(summary = "회의방 나가기", description = "현재 참여중인 회의방을 나갑니다.")
	public void leaveRoom() {
		User user = userService.getUserEntity();
		meetRoomService.leaveRoom(user.getId(), user.getWorkSpace().getId());
	}
}
