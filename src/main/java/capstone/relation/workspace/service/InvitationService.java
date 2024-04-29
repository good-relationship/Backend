package capstone.relation.workspace.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.WorkSpaceMapper;
import capstone.relation.workspace.dto.SpaceState;
import capstone.relation.workspace.dto.response.WorkspaceInfo;
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
		userService.setInvitedWorkspaceId(workSpace.getId());
		WorkspaceInfo dto = WorkSpaceMapper.INSTANCE.toDto(workSpace);
		dto.setSpaceState(SpaceState.INVITED);
		return dto;
	}

	public WorkspaceInfo joinWorkspace(String inviteCode) {
		WorkSpace workSpace = getWorkSpace(inviteCode);
		workSpace.addUser(userService.getUserEntity());
		workSpaceRepository.save(workSpace);
		WorkspaceInfo dto = WorkSpaceMapper.INSTANCE.toDto(workSpace);
		dto.setSpaceState(SpaceState.HAS_WORK_SPACE);
		return dto;
	}

	public String generateInviteCode(String workSpaceId) {
		return tokenProvider.generateInviteCode(workSpaceId);
	}

	private WorkSpace getWorkSpace(String inviteCode) {
		User userEntity = userService.getUserEntity();
		if (userEntity.getWorkSpace() != null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User already has workspace.");
		}

		String workSpaceId = tokenProvider.getWorkSpaceIdByInviteCode(inviteCode);
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid invite code.") {
			});
		return workSpace;
	}

}
