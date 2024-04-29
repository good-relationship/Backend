package capstone.relation.workspace.dto.response;

import capstone.relation.workspace.dto.SpaceState;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceInfo {
	private String spaceState;
	private String workspaceId;
	private String workspaceName;
	// private List<UserInfo> members;

	@Builder
	public WorkspaceInfo(String workspaceId, String workspaceName, String spaceState) {
		this.workspaceId = workspaceId;
		this.workspaceName = workspaceName;
		this.spaceState = spaceState;
	}

	public void setDummy() {
		this.workspaceId = "1234567890";
		this.workspaceName = "캡스톤 디자인";
		this.spaceState = SpaceState.HAS_WORK_SPACE;
		// UserInfo userInfo = new UserInfo();
		// userInfo.setDummy();
		// this.members = List.of(userInfo);
	}
}
