package capstone.relation.workspace.dto.response;

import lombok.Getter;

@Getter
public class InviteCodeResponse {
	private String inviteCode;

	public InviteCodeResponse(String inviteCode) {
		this.inviteCode = inviteCode;
	}
}
