package capstone.relation.api.auth.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class TokenResponse {
	private String grantType;
	private String accessToken;
	private String refreshToken;
	private Long accessTokenExpiredDate;
	private Long refreshTokenExpiredDate;
	private Long memberId;
}
