package capstone.relation.workspace.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.user.UserMapper;
import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.dto.SpaceState;
import capstone.relation.workspace.dto.request.CreateSpaceRequest;
import capstone.relation.workspace.dto.response.InviteCodeResponse;
import capstone.relation.workspace.dto.response.WorkspaceInfo;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.service.SchoolService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
	private final SchoolService schoolService;
	private final UserService userService;
	private final WorkSpaceRepository workSpaceRepository;
	private final InvitationService invitationService;

	public WorkspaceInfo createNewSpace(CreateSpaceRequest request) {
		Optional<School> schoolEntity = schoolService.getSchoolEntity(request.getSchoolName());
		if (schoolEntity.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "School name is not valid.");
		}
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();

		if (workSpace != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has workspace.");
		}
		School school = schoolEntity.get();
		workSpace = new WorkSpace();
		workSpace.setName(request.getWorkspaceName());
		workSpace.addUser(user);
		school.addWorkSpace(workSpace);
		workSpaceRepository.save(workSpace);
		return WorkspaceInfo.builder()
			.workspaceId(workSpace.getId())
			.workspaceName(workSpace.getName())
			.spaceState(SpaceState.HAS_WORK_SPACE)
			.build();
	}

	public WorkspaceInfo inviteSpace(String inviteCode) {
		return invitationService.inviteWorkspace(inviteCode);
	}

	public WorkspaceInfo joinSpace(String inviteCode) {
		return invitationService.joinWorkspace(inviteCode);
	}

	public InviteCodeResponse getInviteCode() {
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have workspace.");
		}
		return new InviteCodeResponse(invitationService.generateInviteCode(workSpace.getId()));
	}

	public WorkspaceInfo getWorkspaceInfo() {
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have workspace.");
		}
		return WorkspaceInfo.builder()
			.workspaceId(workSpace.getId())
			.workspaceName(workSpace.getName())
			.spaceState(SpaceState.HAS_WORK_SPACE)
			.build();
	}

	public List<UserInfoDto> getMemebers() {
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			String invitedWorkspaceId = user.getInvitedWorkspaceId();
			workSpace = workSpaceRepository.findById(invitedWorkspaceId)
				.orElseThrow(
					() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have invited workspace."));
		}
		return workSpace.getUser().stream()
			.map(UserMapper.INSTANCE::toUserInfoDto)
			.collect(Collectors.toList());
	}
}
