package capstone.relation.api.auth.oauth.provider.config;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import capstone.relation.api.auth.AuthProvider;
import capstone.relation.api.auth.oauth.provider.OAuthUserProvider;
import capstone.relation.api.auth.oauth.provider.kakao.KakaoUserProvider;
import capstone.relation.api.auth.oauth.provider.naver.NaverUserProvider;

@Configuration
public class OAuthProviderConfig {

	@Bean
	public Map<AuthProvider, OAuthUserProvider> authRegistrations(
		KakaoUserProvider kakaoUserProvider,
		NaverUserProvider naverUserProvider
	) {
		Map<AuthProvider, OAuthUserProvider> enumMap = new EnumMap<>(AuthProvider.class);
		enumMap.put(AuthProvider.KAKAO, kakaoUserProvider);
		enumMap.put(AuthProvider.NAVER, naverUserProvider);
		return enumMap;
	}
}
