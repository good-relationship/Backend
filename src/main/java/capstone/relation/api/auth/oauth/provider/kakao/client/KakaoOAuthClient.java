package capstone.relation.api.auth.oauth.provider.kakao.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import capstone.relation.api.auth.oauth.provider.kakao.response.KakaoOAuth2Response;
import lombok.extern.slf4j.Slf4j;

@FeignClient(
	name = "KakaoOAuthClient",
	url = "${oauth2.kakao.userInfoUri}",
	fallbackFactory = KakaoOAuthClient.KakaoOAuthClientFallback.class
)
public interface KakaoOAuthClient {
	@GetMapping
	KakaoOAuth2Response getUserInfoFromKakao(@RequestHeader(name = "Authorization") String authorization);

	@Slf4j
	@Component
	class KakaoOAuthClientFallback implements FallbackFactory<KakaoOAuthClient> {
		@Override
		public KakaoOAuthClient create(Throwable cause) {
			log.warn("KaKao OAuth2 오류 {}", cause.getMessage());
			throw new IllegalArgumentException(cause.getMessage());
		}
	}
}
