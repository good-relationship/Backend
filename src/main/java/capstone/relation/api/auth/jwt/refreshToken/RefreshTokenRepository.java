package capstone.relation.api.auth.jwt.refreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
	void save(RefreshToken refreshToken);

	void deleteExpiredToken();

	Optional<RefreshToken> findByKey(String key);
}
