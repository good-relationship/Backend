package capstone.relation.api.auth.jwt.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class TokenResponse {
	@Schema(description = "인증 방식", example = "Bearer")
	private String grantType;
	@Schema(description = "액세스 토큰", example =
		"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTYzNjIwNjIwMCwiaWF0IjoxNjM2MjA1NDAwfQ.7")
	private String accessToken;
	@Schema(description = "리프레시 토큰", example =
		"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTYzNjIwNjIwMCwiaWF0IjoxNjM2MjA1NDAwfQ.7")
	private String refreshToken;
	@Schema(description = "액세스 토큰 만료 시간", example = "1636206200000")
	private Long accessTokenExpiredDate;
	@Schema(description = "리프레시 토큰 만료 시간", example = "1636206200000")
	private Long refreshTokenExpiredDate;
	@Schema(description = "사용자 ID", example = "1")
	private Long memberId;
	@Schema(description = "워크스페이스 여부", example = "hasWorkSpace")
	private WorkspaceStateType hasWorkSpace; //
}
