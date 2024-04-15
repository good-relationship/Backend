package capstone.relation.api.auth.jwt;

import java.io.IOException;
import java.security.Key;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final ObjectMapper objectMapper;
	private final JwtProperties jwtProperties;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String jwt = resolveToken(request);
		if (StringUtils.hasText(jwt)) {
			Claims claims = resolveClaim(jwt, response);
			if (claims == null) {
				return;
			}
			updateSecurityContext(claims, jwt);
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(jwtProperties.getAccessTokenHeader());
		if (StringUtils.hasText(bearerToken)) {
			return bearerToken;
		}
		return null;
	}

	private Claims resolveClaim(String jwt, HttpServletResponse response) throws IOException {
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getTokenSecret());
		Key key = Keys.hmacShaKeyFor(keyBytes);
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException e) {
			expiredTokenResponse(response, e);
		} catch (Exception e) {
			invalidTokenResponse(response, e);
		}
		return null;
	}

	private void expiredTokenResponse(HttpServletResponse response, Exception e) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().println(
			objectMapper.writeValueAsString(
				Map.of("예외", "토큰 만료", "메시지", e.getMessage())
			)
		);
	}

	private void invalidTokenResponse(HttpServletResponse response, Exception e) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().println(
			objectMapper.writeValueAsString(
				Map.of("예외", "잘못된 토큰", "메시지", e.getMessage())
			)
		);
	}

	private void updateSecurityContext(Claims claims, String jwt) {
		String subject = claims.getSubject();
		Long memberId = Long.parseLong(subject);
		String role = claims.get(jwtProperties.getAuthorityKey(), String.class);
		UserDetails principal = SecurityUser.of(memberId, role);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt,
			principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}