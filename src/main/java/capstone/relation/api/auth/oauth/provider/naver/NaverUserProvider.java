package capstone.relation.api.auth.oauth.provider.naver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import capstone.relation.api.auth.jwt.JwtProperties;
import capstone.relation.api.auth.oauth.provider.OAuthUserProvider;
import capstone.relation.api.auth.oauth.provider.naver.client.NaverOAuthClient;
import capstone.relation.api.auth.oauth.provider.naver.client.NaverTokenClient;
import capstone.relation.api.auth.oauth.provider.naver.response.NaverOAuth2Response;
import capstone.relation.user.domain.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaverUserProvider implements OAuthUserProvider {
	private final NaverOAuthClient naverOAuthClient;
	private final NaverTokenClient naverTokenClient;
	private final JwtProperties jwtProperties;

	@Value("${oauth2.naver.client-id}")
	private String clientId;
	@Value("${oauth2.naver.client-secret}")
	private String clientSecret;

	@Override
	public User getUser(String authorizationCode) {
		NaverOAuth2Response response = naverOAuthClient.getUserInfo(
			jwtProperties.getBearerPrefix() + authorizationCode);
		return response.toEntity();
	}

	@Override
	public String getToken(String authorizationCode) {
		ResponseEntity<String> response = naverTokenClient.getToken(
			MediaType.APPLICATION_FORM_URLENCODED,
			"authorization_code",
			clientId,
			clientSecret,
			authorizationCode,
			"YOUR_STATE_VALUE"
		);
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> responseMap = mapper.readValue(response.getBody(),
				new TypeReference<Map<String, String>>() {
				});
			return responseMap.get("access_token");
		} catch (Exception err) {
			throw new RuntimeException("Failed to retrieve access token from Naver", err);
		}
	}
}
