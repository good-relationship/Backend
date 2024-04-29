package capstone.relation.api.auth.oauth.provider.kakao.response;

import java.util.Map;

import capstone.relation.user.domain.Role;
import capstone.relation.user.domain.User;

public record KakaoOAuth2Response(
	long id,
	String connected_at,
	Map<String, String> properties,
	KakaoAccount kakao_account
) {
	public record KakaoAccount(
		boolean profile_nickname_needs_agreement,
		Profile profile,
		boolean has_email,
		boolean email_needs_agreement,
		boolean is_email_valid,
		boolean is_email_verified,
		String email
	) {
		public record Profile(
			String nickname,
			String profile_image_url
		) {
		}
	}

	public User toEntity() {
		return User.builder()
			.provider("kakao")
			.email(kakao_account.email())
			.userName(kakao_account.profile().nickname())
			.role(Role.USER)
			.profileImage(kakao_account.profile().profile_image_url())
			.build();
	}
}
