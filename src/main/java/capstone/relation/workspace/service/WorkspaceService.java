package capstone.relation.workspace.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import capstone.relation.api.auth.jwt.response.WorkspaceStateType;
import capstone.relation.user.UserMapper;
import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.dto.request.CreateSpaceRequest;
import capstone.relation.workspace.dto.response.InviteCodeResponse;
import capstone.relation.workspace.dto.response.WorkspaceInfo;
import capstone.relation.workspace.exception.WorkSpaceErrorCode;
import capstone.relation.workspace.exception.WorkSpaceException;
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
			throw new WorkSpaceException(WorkSpaceErrorCode.INVALID_SCHOOL);
		}
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();

		if (workSpace != null) {
			throw new WorkSpaceException(WorkSpaceErrorCode.ALREADY_WORKSPACE_MEMBER);
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
			.spaceState(WorkspaceStateType.HAS_WORKSPACE)
			.schoolName(school.getSchoolName())
			.build();
	}

	public WorkspaceInfo inviteSpace(String inviteCode) {
		return invitationService.inviteWorkspace(inviteCode);
	}

	public WorkspaceInfo joinSpace() {
		return invitationService.joinWorkspace();
	}

	public InviteCodeResponse getInviteCode() {
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null)
			throw new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE);

		return new InviteCodeResponse(invitationService.generateInviteCode(workSpace.getId()));
	}

	public WorkspaceInfo getWorkspaceInfo() {
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			String invitedWorkspaceId = user.getInvitedWorkspaceId();
			if (invitedWorkspaceId == null || invitedWorkspaceId.isEmpty()) {
				return WorkspaceInfo.builder()
					.spaceState(WorkspaceStateType.NO_SPACE)
					.build();
			}
			workSpace = workSpaceRepository.findById(invitedWorkspaceId)
				.orElse(null);
			if (workSpace == null) {
				return WorkspaceInfo.builder()
					.spaceState(WorkspaceStateType.NO_SPACE)
					.build();
			}
			return WorkspaceInfo.builder()
				.workspaceId(workSpace.getId())
				.workspaceName(workSpace.getName())
				.spaceState(WorkspaceStateType.INVITED)
				.build();
		}
		return WorkspaceInfo.builder()
			.workspaceId(workSpace.getId())
			.workspaceName(workSpace.getName())
			.schoolName(workSpace.getSchool().getSchoolName())
			.spaceState(WorkspaceStateType.HAS_WORKSPACE)
			.build();
	}

	public List<UserInfoDto> getMemebers() {
		User user = userService.getUserEntity();
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			String invitedWorkspaceId = user.getInvitedWorkspaceId();
			if (invitedWorkspaceId == null || invitedWorkspaceId.isEmpty())
				throw new WorkSpaceException(WorkSpaceErrorCode.NOT_INVITED_USER);
			workSpace = workSpaceRepository.findById(invitedWorkspaceId)
				.orElseThrow(
					() -> new WorkSpaceException(WorkSpaceErrorCode.NOT_INVITED_USER));
		}
		return workSpace.getUser().stream()
			.map(UserMapper.INSTANCE::toUserInfoDto)
			.collect(Collectors.toList());
	}
}
