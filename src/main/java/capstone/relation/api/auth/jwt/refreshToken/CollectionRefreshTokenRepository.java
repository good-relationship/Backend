package capstone.relation.api.auth.jwt.refreshToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class CollectionRefreshTokenRepository implements RefreshTokenRepository {
	private final List<RefreshToken> refreshTokenList = new ArrayList<>();

	@Override
	public void save(RefreshToken refreshToken) {
		refreshTokenList.add(refreshToken);
	}

	@Override
	public void deleteExpiredToken() {
		refreshTokenList.removeIf(refreshToken -> refreshToken.expiredDate().before(new Date()));
	}

	@Override
	public Optional<RefreshToken> findByKey(String key) {
		return refreshTokenList.stream()
			.filter(refreshToken -> refreshToken.key().equals(key))
			.findFirst();
	}
}
