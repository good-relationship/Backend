package capstone.relation.api.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
	
	INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "access token이 이미 만료되었거나 올바르지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token이 이미 만료되었거나 올바르지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

