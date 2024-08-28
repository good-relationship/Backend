package capstone.relation.api.auth.exception;

import capstone.relation.global.exception.GlobalCodeException;
import lombok.Getter;

@Getter
public class AuthException extends GlobalCodeException {

	public AuthException(AuthErrorCode errorCode) {
		super(errorCode);
	}
}

