package capstone.relation.api.auth.oauth.provider.naver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	name = "NaverTokenClient",
	url = "${oauth2.naver.tokenUri}"
)
public interface NaverTokenClient {

	@PostMapping
	ResponseEntity<String> getToken(
		@RequestHeader(name = "Content-Type") MediaType contentType,
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("code") String code,
		@RequestParam("state") String state
	);
}
