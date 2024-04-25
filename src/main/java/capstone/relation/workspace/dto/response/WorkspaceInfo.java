package capstone.relation.workspace.dto.response;

import capstone.relation.workspace.dto.SpaceState;
import lombok.Data;

@Data
public class WorkspaceInfo {
	private String spaceState;
	private String workspaceId;
	private String workspaceName;
	// private List<UserInfo> members;

	public void setDummy() {
		this.workspaceId = "1234567890";
		this.workspaceName = "캡스톤 디자인";
		this.spaceState = SpaceState.HAS_WORK_SPACE;
		// UserInfo userInfo = new UserInfo();
		// userInfo.setDummy();
		// this.members = List.of(userInfo);
	}
}
