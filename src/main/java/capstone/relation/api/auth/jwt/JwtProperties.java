package capstone.relation.api.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "oauth2.jwt")
@Component
@Setter
@Getter
public class JwtProperties {
	private long accessTokenExpireDay;
	private long accessTokenExpireHour;
	private long accessTokenExpireMin;
	private long accessTokenExpireSec;

	private long refreshTokenExpireDay;
	private long refreshTokenExpireHour;
	private long refreshTokenExpireMin;
	private long refreshTokenExpireSec;

	private String authorityKey;
	private String bearerPrefix;
	private String tokenSecret;
	private String accessTokenHeader;

	public long getAccessTokenExpireTime() {
		return dayToMilliSec(accessTokenExpireDay, accessTokenExpireHour, accessTokenExpireMin, accessTokenExpireSec);
	}

	public long getRefreshTokenExpireTime() {
		return dayToMilliSec(accessTokenExpireDay, accessTokenExpireHour, accessTokenExpireMin, accessTokenExpireSec);
	}

	private long dayToMilliSec(long day, long hour, long min, long sec) {
		return 1000 * (sec + 60 * (min + 60 * (hour + 24 * day)));
		// return 1000 * 60 * 60 * 24 * day;
	}

}
