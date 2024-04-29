package capstone.relation.user.dto;

import lombok.Data;

@Data
public class UserInfoDto {
	private String userId;
	private String userName;
	private String userImage;
	private String email;

	public void setDummy() {
		this.userId = "1234567890";
		this.userName = "신중은";
		this.userImage = "https://lh3.googleusercontent.com/ogw/AF2bZyhqowurXq6imx61oPHn5G_c6OIEnucOyJanitxYGFUI498=s32-c-mo";
		this.email = "wnddms12345@naver.com";
	}
}
