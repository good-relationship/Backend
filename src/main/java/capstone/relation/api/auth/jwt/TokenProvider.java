package capstone.relation.api.auth.jwt;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.api.auth.jwt.refreshToken.CollectionRefreshTokenRepository;
import capstone.relation.api.auth.jwt.refreshToken.RefreshToken;
import capstone.relation.api.auth.jwt.refreshToken.RefreshTokenRepository;
import capstone.relation.api.auth.jwt.response.TokenResponse;
import capstone.relation.user.domain.Role;
import capstone.relation.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {
	private final JwtProperties jwtProperties;
	private final RefreshTokenRepository refreshTokenRepository;
	private final Key key;

	public TokenProvider(
		JwtProperties jwtProperties,
		CollectionRefreshTokenRepository refreshTokenRepository
	) {
		this.jwtProperties = jwtProperties;
		this.refreshTokenRepository = refreshTokenRepository;
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getTokenSecret());
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public TokenResponse generateTokenResponse(User user) {
		long now = (new Date().getTime());
		Date accessTokenExpiredDate = new Date(now + jwtProperties.getAccessTokenExpireTime());
		Date refreshTokenExpiredDate = new Date(now + jwtProperties.getRefreshTokenExpireTime());
		String accessToken = generateAccessToken(user, accessTokenExpiredDate);
		RefreshToken refreshToken = generateRefreshToken(user, refreshTokenExpiredDate);
		refreshTokenRepository.save(refreshToken);
		return TokenResponse.builder()
			.grantType(jwtProperties.getBearerPrefix())
			.accessToken(accessToken)
			.refreshToken(refreshToken.key())
			.accessTokenExpiredDate(accessTokenExpiredDate.getTime())
			.refreshTokenExpiredDate(refreshTokenExpiredDate.getTime())
			.memberId(user.getId())
			.build();
	}

	public String generateAccessTokenByRefreshToken(String refreshTokenKey) {
		long now = (new Date().getTime());
		RefreshToken refreshToken = refreshTokenRepository.findByKey(refreshTokenKey)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "유효하지 않은 리프레시 토큰 입니다."));
		User user = refreshToken.user();
		Date accessTokenExpiredDate = new Date(now + jwtProperties.getAccessTokenExpireTime());
		return generateAccessToken(user, accessTokenExpiredDate);
	}

	public Authentication getAuthentication(String accessToken) {
		Claims claims = decodeAccessToken(accessToken);
		Long userId = Long.parseLong(claims.getSubject());

		String authorityKey = Role.USER.getKey() + claims.get(jwtProperties.getAuthorityKey(), String.class);
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityKey);

		return new UsernamePasswordAuthenticationToken(userId, null, Collections.singleton(authority));
	}

	public String generateInviteCode(String workSpaceId) {
		String jwt = Jwts.builder()
			.setSubject(workSpaceId)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
		return URLEncoder.encode(jwt, StandardCharsets.UTF_8);
	}

	public String getWorkSpaceIdByInviteCode(String inviteCode) {
		try {
			String inviteToken = URLDecoder.decode(inviteCode, StandardCharsets.UTF_8);
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(inviteToken).getBody();
			return claims.getSubject();
		} catch (ExpiredJwtException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "만료된 초대 코드입니다.");
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 코드입니다.");
		}
	}

	private String generateAccessToken(User user, Date accessTokenExpiredDate) {

		return Jwts.builder()
			.setSubject(String.valueOf(user.getId()))
			.claim(jwtProperties.getAuthorityKey(), user.getRole().toString())
			.setExpiration(accessTokenExpiredDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	private RefreshToken generateRefreshToken(User user, Date refreshTokenExpiredDate) {
		String refreshToken = Jwts.builder()
			.setExpiration(refreshTokenExpiredDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
		return new RefreshToken(refreshToken, user, refreshTokenExpiredDate);
	}

	private Claims decodeAccessToken(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException expiredJwtException) {
			throw new IllegalStateException("만료된 토큰입니다.");
		}
	}
}
