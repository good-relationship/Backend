package capstone.relation.workspace.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.user.UserService;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.workspace.dto.request.CreateSpaceRequest;
import capstone.relation.workspace.dto.response.InviteCodeResponse;
import capstone.relation.workspace.dto.response.SchoolsResponse;
import capstone.relation.workspace.dto.response.WorkspaceInfo;
import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.service.SchoolService;
import capstone.relation.workspace.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Workspace", description = "워크스페이스 관련 API")
@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {
	private final SchoolService schoolService;
	private final WorkspaceService workspaceService;
	private final UserService userService;

	@GetMapping("/member")
	@Operation(summary = "워크스페이스 멤버 조회", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 멤버 조회 성공",
			content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
	})
	public List<UserInfoDto> getMember() {
		return workspaceService.getMemebers();
	}

	@GetMapping("/info")
	@Operation(summary = "워크스페이스 정보 조회", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = WorkspaceInfo.class))),
	})
	public WorkspaceInfo getInfo() {
		return workspaceService.getWorkspaceInfo();
	}

	@PostMapping("/join")
	@Operation(summary = "워크스페이스 가입 이전에 초대 받은 유저는 join을 콜하면 가입됩니다.", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 가입 성공",
			content = @Content(schema = @Schema(implementation = WorkspaceInfo.class))),
	})
	public WorkspaceInfo join() {
		return workspaceService.joinSpace();
	}

	@PostMapping("/leave")
	@Operation(summary = "워크스페이스 탈퇴", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 탈퇴 성공"),
	})
	public void leave() {
		userService.leaveWorkspace();
	}

	@PostMapping("/invited")
	@Operation(summary = "워크스페이스 초대 알림", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 초대 알림 성공",
			content = @Content(schema = @Schema(implementation = WorkspaceInfo.class))),
	})
	public WorkspaceInfo invited(@RequestParam String inviteCode) {
		return workspaceService.inviteSpace(inviteCode);
	}

	@GetMapping("/inviteCode")
	@Operation(summary = "워크스페이스 초대 코드 조회", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 초대 코드 조회 성공",
			content = @Content(schema = @Schema(implementation = InviteCodeResponse.class))),
	})
	public InviteCodeResponse invite() {
		return workspaceService.getInviteCode();
	}

	@GetMapping("/school")
	@Operation(summary = "학교 정보 조회", responses = {
		@ApiResponse(responseCode = "200", description = "학교 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = SchoolsResponse.class)))
	})
	public SchoolsResponse getSchool(@RequestParam String name) {
		List<School> schools = schoolService.searchSchool(name);
		SchoolsResponse schoolsResponse = new SchoolsResponse();
		schoolsResponse.setBySchools(schools);
		return schoolsResponse;
	}

	@PostMapping("/create")
	@Operation(summary = "워크스페이스 생성", responses = {
		@ApiResponse(responseCode = "200", description = "워크스페이스 생성 성공")
	})
	public WorkspaceInfo create(@Valid @RequestBody CreateSpaceRequest createSpaceRequest) {
		return workspaceService.createNewSpace(createSpaceRequest);
	}
}
