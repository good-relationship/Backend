package capstone.relation.workspace.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class InviteCodeResponse {
	@Schema(description = "초대 코드", example = "1234567890")
	private String inviteCode;

	public InviteCodeResponse(String inviteCode) {
		this.inviteCode = inviteCode;
	}
}
