package capstone.relation.workspace.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.workspace.dto.SpaceState;
import capstone.relation.workspace.dto.request.CreateSpaceRequest;
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

	@GetMapping("/member")
	public List<UserInfoDto> getMember() {
		List<UserInfoDto> members = new ArrayList<>();
		UserInfoDto userInfoDto1 = new UserInfoDto();
		userInfoDto1.setDummy();
		members.add(userInfoDto1);
		UserInfoDto userInfoDto2 = new UserInfoDto();
		userInfoDto2.setDummy();
		members.add(userInfoDto2);
		return members;
	}

	@GetMapping("/info")
	public WorkspaceInfo getInfo() {
		WorkspaceInfo workspaceInfo = new WorkspaceInfo();
		workspaceInfo.setDummy();
		return workspaceInfo;
	}

	@PostMapping("/join")
	public WorkspaceInfo join(@RequestParam String inviteCode) {
		WorkspaceInfo workspaceInfo = new WorkspaceInfo();
		workspaceInfo.setDummy();
		workspaceInfo.setSpaceState(SpaceState.HAS_WORK_SPACE);
		return workspaceInfo;
	}

	@PostMapping("/invited")
	public WorkspaceInfo invited(@RequestParam String inviteCode) {
		WorkspaceInfo workspaceInfo = new WorkspaceInfo();
		workspaceInfo.setDummy();
		workspaceInfo.setSpaceState(SpaceState.INVITED);
		return workspaceInfo;
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
