package capstone.relation.api.auth.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
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
		authService.cookieLogin(AuthProvider.NAVER, code, response);
	}

	@GetMapping("/code/kakao")
	public void getKakaoCode(@RequestParam String code, HttpServletResponse response) throws IOException {
		authService.cookieLogin(AuthProvider.KAKAO, code, response);
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
