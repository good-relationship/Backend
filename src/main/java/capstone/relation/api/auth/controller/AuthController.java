package capstone.relation.api.auth.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.api.auth.AuthProvider;
import capstone.relation.api.auth.jwt.response.RefreshTokenResponse;
import capstone.relation.api.auth.jwt.response.TokenResponse;
import capstone.relation.api.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@GetMapping("/code/naver")
	public void getNaverCode(@RequestParam String code, HttpServletResponse response) throws IOException {
		String accessToken = authService.getToken(AuthProvider.NAVER, code);
		System.out.println("accessToken = " + accessToken);
		TokenResponse tokenResponse = authService.login(AuthProvider.NAVER, "Bearer " + accessToken);
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

	@GetMapping("/code/kakao")
	public void getKakaoCode(@RequestParam String code, HttpServletResponse response) throws IOException {
		String accessToken = authService.getToken(AuthProvider.KAKAO, code);
		System.out.println("accessToken = " + accessToken);
		TokenResponse tokenResponse = authService.login(AuthProvider.KAKAO, "Bearer " + accessToken);
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

	@PostMapping(value = "/kakao", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenResponse> loginWithKakao(@RequestHeader("Authorization") String accessToken) {
		return ResponseEntity.ok(authService.login(AuthProvider.KAKAO, accessToken));
	}

	@PostMapping(value = "/naver", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenResponse> loginWithNaver(@RequestHeader("Authorization") String accessToken) {
		return ResponseEntity.ok(authService.login(AuthProvider.NAVER, accessToken));
	}

	@PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RefreshTokenResponse> refresh(@RequestHeader("Refresh") String refreshToken) {
		return ResponseEntity.ok(authService.generateAccessToken(refreshToken));
	}

}
