package capstone.relation.api.auth.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.api.auth.AuthProvider;
import capstone.relation.api.auth.domain.User;
import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.api.auth.jwt.response.RefreshTokenResponse;
import capstone.relation.api.auth.jwt.response.TokenResponse;
import capstone.relation.api.auth.oauth.provider.OAuthUserProvider;
import capstone.relation.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final Map<AuthProvider, OAuthUserProvider> authRegistrations;
	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;

	@Transactional
	public TokenResponse login(AuthProvider authProvider, String accessToken) {
		User user = authRegistrations.get(authProvider).getUser(accessToken);
		User savedUser = saveOrUpdate(user);
		return tokenProvider.generateTokenResponse(savedUser);
	}

	private User saveOrUpdate(User user) {
		User savedMember = userRepository.findByEmailAndProvider(user.getEmail(), user.getProvider()).orElse(user);
		return userRepository.save(savedMember);
	}

	public RefreshTokenResponse generateAccessToken(String refreshToken) {
		String generatedToken = tokenProvider.generateAccessTokenByRefreshToken(refreshToken);
		return new RefreshTokenResponse(generatedToken);
	}

	public String getToken(AuthProvider authProvider, String code) {
		return authRegistrations.get(authProvider).getToken(code);
	}
}
