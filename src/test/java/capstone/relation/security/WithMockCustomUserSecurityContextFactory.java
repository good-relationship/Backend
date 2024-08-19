package capstone.relation.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import capstone.relation.api.auth.jwt.SecurityUser;

public class WithMockCustomUserSecurityContextFactory
	implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		SecurityUser securityUser = SecurityUser.of(customUser.id(), customUser.role());
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, null,
			securityUser.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}
