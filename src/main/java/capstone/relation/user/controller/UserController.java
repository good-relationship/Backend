package capstone.relation.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.meeting.service.MeetRoomService;
import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.user.dto.RoomInfoDto;
import capstone.relation.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "사용자 정보 조회")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final MeetRoomService meetRoomService;

	@GetMapping("/info")
	@Operation(summary = "사용자 정보 조회 액션확인", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	public UserInfoDto getInfo() {
		return userService.getUserInfo();
	}

	@GetMapping("/room/info")
	@Operation(summary = "사용자 방 정보 조회", description = "현재 로그인한 사용자의 방 정보를 조회합니다.")
	public RoomInfoDto getRoomInfo() {
		User user = userService.getUserEntity();
		Long userId = user.getId();
		String workspaceId = user.getWorkSpace().getId();
		return meetRoomService.getRoomInfo(workspaceId, userId);
	}
}
