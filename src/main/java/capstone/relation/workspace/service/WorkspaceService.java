package capstone.relation.workspace.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.workspace.dto.request.CreateSpaceRequest;
import capstone.relation.workspace.dto.response.WorkspaceInfo;
import capstone.relation.workspace.school.service.SchoolService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
	private final SchoolService schoolService;

	public WorkspaceInfo createNewSpace(CreateSpaceRequest request) {
		if (!schoolService.isExistSchool(request.getSchoolName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "School name is not valid.");
		}
		WorkspaceInfo workspaceInfo = new WorkspaceInfo();
		workspaceInfo.setDummy();
		return workspaceInfo;
	}
}
