package capstone.relation.api.auth.jwt.refreshToken;

import java.util.Date;

import capstone.relation.api.auth.domain.User;

public record RefreshToken(
	String key,
	User user,
	Date expiredDate) {
}
