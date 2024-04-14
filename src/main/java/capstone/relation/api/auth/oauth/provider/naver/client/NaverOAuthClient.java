package capstone.relation.api.auth.oauth.provider.naver.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import capstone.relation.api.auth.oauth.provider.naver.response.NaverOAuth2Response;
import lombok.extern.slf4j.Slf4j;

@FeignClient(
	name = "NaverOAuthClient",
	url = "${oauth2.naver.userInfoUri}",
	fallbackFactory = NaverOAuthClient.NaverOAuthClientFallback.class
)
public interface NaverOAuthClient {
	@GetMapping
	NaverOAuth2Response getUserInfo(@RequestHeader(name = "Authorization") String authorization);

	@Slf4j
	@Component
	class NaverOAuthClientFallback implements FallbackFactory<NaverOAuthClient> {
		@Override
		public NaverOAuthClient create(Throwable cause) {
			log.warn("Naver OAuth2 오류 {}", cause.getMessage());
			throw new IllegalArgumentException(cause.getMessage());
		}
	}
}
