package capstone.relation.workspace.service;

import org.springframework.stereotype.Service;

import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.api.auth.jwt.response.WorkspaceStateType;
import capstone.relation.user.UserService;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.WorkSpaceMapper;
import capstone.relation.workspace.dto.response.WorkspaceInfo;
import capstone.relation.workspace.exception.WorkSpaceErrorCode;
import capstone.relation.workspace.exception.WorkSpaceException;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {
	private final TokenProvider tokenProvider;
	private final UserService userService;
	private final WorkSpaceRepository workSpaceRepository;

	public WorkspaceInfo inviteWorkspace(String inviteCode) {
		WorkSpace workSpace = getWorkSpace(inviteCode);
		if (workSpace.getUsers().contains(userService.getUserEntity())) {
			throw new WorkSpaceException(WorkSpaceErrorCode.ALREADY_WORKSPACE_MEMBER);
		}
		userService.setInvitedWorkspaceId(workSpace.getId());
		WorkspaceInfo dto = WorkSpaceMapper.INSTANCE.toDto(workSpace);
		dto.setSpaceState(WorkspaceStateType.INVITED);
		return dto;
	}

	public WorkspaceInfo joinWorkspace() {
		String invitedWorkspaceId = userService.getUserEntity().getInvitedWorkspaceId();
		if (invitedWorkspaceId == null) {
			throw new WorkSpaceException(WorkSpaceErrorCode.NOT_INVITED_USER);
		}
		WorkSpace workSpace = workSpaceRepository.findById(invitedWorkspaceId).orElseThrow(
			() -> new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE));
		workSpace.addUser(userService.getUserEntity());
		workSpaceRepository.save(workSpace);
		WorkspaceInfo dto = WorkSpaceMapper.INSTANCE.toDto(workSpace);
		dto.setSpaceState(WorkspaceStateType.HAS_WORKSPACE);
		return dto;
	}

	public String generateInviteCode(String workSpaceId) {
		return tokenProvider.generateInviteCode(workSpaceId);
	}

	public WorkSpace getWorkSpace(String inviteCode) {

		String workSpaceId = tokenProvider.getWorkSpaceIdByInviteCode(inviteCode);
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElseThrow(
			() -> new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE));
		return workSpace;
	}

}
