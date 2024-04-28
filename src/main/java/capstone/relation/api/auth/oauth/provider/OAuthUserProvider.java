package capstone.relation.api.auth.oauth.provider;

import capstone.relation.user.domain.User;

public interface OAuthUserProvider {
	User getUser(String authorization);

	String getToken(String authorization);
}
