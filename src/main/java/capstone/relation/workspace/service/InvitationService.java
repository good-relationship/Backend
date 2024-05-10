package capstone.relation.workspace.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.api.auth.jwt.response.WorkspaceStateType;
import capstone.relation.user.UserService;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.WorkSpaceMapper;
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
		if (workSpace.getUser().contains(userService.getUserEntity())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 가입된 워크스페이스입니다.");
		}
		userService.setInvitedWorkspaceId(workSpace.getId());
		WorkspaceInfo dto = WorkSpaceMapper.INSTANCE.toDto(workSpace);
		dto.setSpaceState(WorkspaceStateType.INVITED);
		return dto;
	}

	public WorkspaceInfo joinWorkspace() {
		String invitedWorkspaceId = userService.getUserEntity().getInvitedWorkspaceId();
		if (invitedWorkspaceId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이전에 초대를 하지 않은 유저가 접근합니다.");
		}
		WorkSpace workSpace = workSpaceRepository.findById(invitedWorkspaceId).orElseThrow(
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "초대코드에 해당하는 워크스페이스가 없습니다."));
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
			() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "초대코드에 해당하는 워크스페이스가 없습니다."));
		return workSpace;
	}

}
