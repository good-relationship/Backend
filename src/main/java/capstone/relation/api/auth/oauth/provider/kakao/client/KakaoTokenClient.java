package capstone.relation.api.auth.oauth.provider.kakao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	name = "KakaoTokenClient",
	url = "${oauth2.kakao.tokenUri}"
)
public interface KakaoTokenClient {

	@PostMapping
	ResponseEntity<String> getToken(
		@RequestHeader(name = "Content-Type") String contentType,
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("redirect_uri") String redirectUri,
		@RequestParam("code") String code,
		@RequestParam("client_secret") String clientSecret
	);
}
