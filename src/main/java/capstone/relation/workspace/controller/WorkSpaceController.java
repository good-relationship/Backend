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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {
	private final SchoolService schoolService;
	private final WorkspaceService workspaceService;
	private final UserService userService;

	@GetMapping("/member")
	public List<UserInfoDto> getMember() {
		return workspaceService.getMemebers();
	}

	@GetMapping("/info")
	public WorkspaceInfo getInfo() {
		return workspaceService.getWorkspaceInfo();
	}

	@PostMapping("/join")
	public WorkspaceInfo join(@RequestParam String inviteCode) {
		return workspaceService.joinSpace(inviteCode);
	}

	@PostMapping("/leave")
	public void leave() {
		userService.leaveWorkspace();
	}

	@PostMapping("/invited")
	public WorkspaceInfo invited(@RequestParam String inviteCode) {
		return workspaceService.inviteSpace(inviteCode);
	}
	
	@GetMapping("/inviteCode")
	public InviteCodeResponse invite() {
		return workspaceService.getInviteCode();
	}

	@GetMapping("/school")
	public SchoolsResponse getSchool(@RequestParam String name) {
		List<School> schools = schoolService.searchSchool(name);
		SchoolsResponse schoolsResponse = new SchoolsResponse();
		schoolsResponse.setBySchools(schools);
		return schoolsResponse;
	}

	@PostMapping("/create")
	public WorkspaceInfo create(@Valid @RequestBody CreateSpaceRequest createSpaceRequest) {
		return workspaceService.createNewSpace(createSpaceRequest);
	}
}
