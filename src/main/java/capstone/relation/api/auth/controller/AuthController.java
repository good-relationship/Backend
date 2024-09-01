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
import capstone.relation.api.auth.docs.LoginAuthExceptionDocs;
import capstone.relation.api.auth.docs.RefreshAuthExceptionDocs;
import capstone.relation.api.auth.jwt.response.RefreshTokenResponse;
import capstone.relation.api.auth.jwt.response.TokenResponse;
import capstone.relation.api.auth.service.AuthService;
import capstone.relation.global.annotation.ApiErrorExceptionsExample;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "로그인 인증 API")
@RestController
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@Hidden
	@GetMapping("/code/naver")
	public void getNaverCode(@RequestParam String code, HttpServletResponse response) throws IOException {
		authService.cookieLogin(AuthProvider.NAVER, code, response);
	}

	@Hidden
	@GetMapping("/code/kakao")
	public void getKakaoCode(@RequestParam String code, HttpServletResponse response) throws IOException {
		authService.cookieLogin(AuthProvider.KAKAO, code, response);
	}

	@PostMapping(value = "/kakao", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "카카오 로그인 인증", description = "초대받은 경우만 있는 토큰으로 카카오를 통한 로그인 인증을 처리합니다. "
		+ "초대 토큰이 없는 경우에는 `inviteToken` 없이 요청합니다.")
	@ApiErrorExceptionsExample(LoginAuthExceptionDocs.class)
	public ResponseEntity<TokenResponse> loginWithKakaoCode(
		@Parameter(description = "카카오에서 받아온 AuthorizationCode", required = true, example = "네이버에서 받아온 코드")
		@RequestParam String code,
		@Parameter(description = "초대된 경우에만 있는 코드", required = false, example = "초대 코드")
		@RequestParam(required = false) String inviteCode) {
		return ResponseEntity.ok(authService.loginWithCode(AuthProvider.KAKAO, code, inviteCode));
	}

	@PostMapping(value = "/naver", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "네이버 로그인 인증", description = "카카오 로그인 인증과 유사하게 처리합니다. "
		+ "초대받은 경우만 있는 토큰으로 네이버를 통한 로그인 인증을 처리합니다. 초대 토큰이 없는 경우에는 `inviteToken` 없이 요청합니다.")
	@ApiResponse(responseCode = "200", description = "Successful operation",
		content = @Content(schema = @Schema(implementation = TokenResponse.class)))
	@ApiErrorExceptionsExample(LoginAuthExceptionDocs.class)
	public ResponseEntity<TokenResponse> loginWithNaverCode(@RequestParam String code,
		@RequestParam(required = false) String inviteCode) {
		return ResponseEntity.ok(authService.loginWithCode(AuthProvider.NAVER, code, inviteCode));
	}

	@PostMapping(value = "/refresh")
	@Operation(summary = "AccessToken 갱신", description = "Refresh Token을 통해 AccessToken을 갱신합니다.")
	@ApiErrorExceptionsExample(RefreshAuthExceptionDocs.class)
	public ResponseEntity<RefreshTokenResponse> refresh(@RequestHeader("Refresh") String refreshToken) {
		return ResponseEntity.ok(authService.generateAccessToken(refreshToken));
	}

	@Hidden
	@PostMapping(value = "/token/kakao", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenResponse> loginWithKakao(@RequestHeader("Authorization") String accessToken) {
		return ResponseEntity.ok(authService.login(AuthProvider.KAKAO, accessToken));
	}

	@Hidden
	@PostMapping(value = "/token/naver", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TokenResponse> loginWithNaver(@RequestHeader("Authorization") String accessToken) {
		return ResponseEntity.ok(authService.login(AuthProvider.NAVER, accessToken));
	}

}
