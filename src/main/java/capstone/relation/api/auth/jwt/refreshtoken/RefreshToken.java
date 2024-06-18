package capstone.relation.api.auth.jwt.refreshtoken;

import java.util.Date;

import capstone.relation.user.domain.User;

public record RefreshToken(
	String key,
	User user,
	Date expiredDate) {
}
