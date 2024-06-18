package capstone.relation.workspace.dto.response;

import capstone.relation.api.auth.jwt.response.WorkspaceStateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceInfo {
	@Schema(description = "작업 공간 ID", example = "1234567890")
	private String workspaceId;
	@Schema(description = "작업 공간 상태", example = "hasWorkSpace")
	private WorkspaceStateType spaceState;
	@Schema(description = "작업 공간 이름", example = "캡스톤 디자인 조은사이")
	private String workspaceName;
	// private List<UserInfo> members;
	@Schema(description = "학교 이름", example = "조은대학교")
	private String schoolName;

	@Builder
	public WorkspaceInfo(String workspaceId, String workspaceName, WorkspaceStateType spaceState, String schoolName) {
		this.workspaceId = workspaceId;
		this.workspaceName = workspaceName;
		this.spaceState = spaceState;
		this.schoolName = schoolName;
	}
}
