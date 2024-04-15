package capstone.relation.api.auth.oauth.provider.kakao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import capstone.relation.api.auth.domain.User;
import capstone.relation.api.auth.oauth.provider.OAuthUserProvider;
import capstone.relation.api.auth.oauth.provider.kakao.client.KakaoOAuthClient;
import capstone.relation.api.auth.oauth.provider.kakao.client.KakaoTokenClient;
import capstone.relation.api.auth.oauth.provider.kakao.response.KakaoOAuth2Response;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoUserProvider implements OAuthUserProvider {
	private final KakaoOAuthClient kakaoClient;
	private final KakaoTokenClient kakaoTokenClient;

	@Value("${oauth2.kakao.client-id}")
	private String clientId;

	@Value("${oauth2.kakao.client-secret}")
	private String clientSecret;

	@Value("${oauth2.kakao.redirect-uri}")
	private String redirectUri;

	@Override
	public User getUser(String authorizationCode) {
		KakaoOAuth2Response response = kakaoClient.getUserInfoFromKakao(authorizationCode);
		return response.toEntity();
	}

	@Override
	public String getToken(String authorizationCode) {

		ResponseEntity<String> response = kakaoTokenClient.getToken(
			"application/x-www-form-urlencoded;charset=utf-8",
			"authorization_code",
			clientId,
			redirectUri,
			authorizationCode,
			clientSecret
		);
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> responseMap = mapper.readValue(response.getBody(),
				new TypeReference<Map<String, String>>() {
				});
			return responseMap.get("access_token");
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve access token from Kakao", e);
		}
	}
}
