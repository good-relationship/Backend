package capstone.relation.api.auth.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.api.auth.AuthProvider;
import capstone.relation.api.auth.domain.User;
import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.api.auth.jwt.response.RefreshTokenResponse;
import capstone.relation.api.auth.jwt.response.TokenResponse;
import capstone.relation.api.auth.oauth.provider.OAuthUserProvider;
import capstone.relation.api.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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

	public void cookieLogin(AuthProvider authProvider, String code, HttpServletResponse response) throws
		IOException {
		String accessToken = getToken(authProvider, code);
		System.out.println("accessToken = " + accessToken);
		TokenResponse tokenResponse = login(authProvider, "Bearer " + accessToken);
		// 액세스 토큰 쿠키 설정
		ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenResponse.getAccessToken())
			.httpOnly(true) // JavaScript 접근 방지
			.secure(true) // HTTPS만 허용
			.sameSite("Strict") // 동일 출처 요구
			.maxAge(tokenResponse.getAccessTokenExpiredDate())
			.path("/")
			.build();

		// 리프레시 토큰 쿠키 설정
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
			.httpOnly(true)
			.secure(true)
			.sameSite("Strict")
			.maxAge(tokenResponse.getRefreshTokenExpiredDate())
			.path("/")
			.build();

		// 토큰 쿠키들을 응답에 추가
		response.addHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		// 리다이렉션 또는 다른 처리 로직
		response.sendRedirect("http://localhost:3000/login");
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
