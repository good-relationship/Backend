package capstone.relation.workspace.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.dto.SpaceState;
import capstone.relation.workspace.dto.request.CreateSpaceRequest;
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
}
