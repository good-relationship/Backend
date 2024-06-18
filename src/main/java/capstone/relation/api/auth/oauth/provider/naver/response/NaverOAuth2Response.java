package capstone.relation.api.auth.oauth.provider.naver.response;

import capstone.relation.user.domain.Role;
import capstone.relation.user.domain.User;

public record NaverOAuth2Response(String resultCode, String message, UserInfo response) {

	public User toEntity() {
		return User.builder()
			.email(response.email())
			.userName(response.name())
			.profileImage(response.profile_image())
			.provider("naver")
			.role(Role.USER)
			.build();
	}

	public record UserInfo(String id, String email, String name, String profile_image) {
	}
}
