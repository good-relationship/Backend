package capstone.relation.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoDto {
	@Schema(description = "사용자 ID", example = "1234567890")
	private String userId;
	@Schema(description = "사용자 이름", example = "신중은")
	private String userName;
	@Schema(description = "사용자 이미지", example = "https://lh3.googleusercontent.com/ogw/AF2bZyhqowurXq6imx61oPHn5G_c6OIEnucOyJanitxYGFUI498=s32-c-mo")
	private String userImage;
	@Schema(description = "사용자 이메일", example = "wnddms12345@naver.com")
	private String email;
}
