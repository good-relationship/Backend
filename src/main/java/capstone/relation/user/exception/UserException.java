package capstone.relation.user.exception;

import capstone.relation.global.exception.GlobalCodeException;

public class UserException extends GlobalCodeException {
	public UserException(UserErrorCode errorCode) {
		super(errorCode);
	}
}
