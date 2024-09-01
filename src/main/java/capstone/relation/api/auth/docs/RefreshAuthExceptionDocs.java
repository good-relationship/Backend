package capstone.relation.api.auth.docs;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.global.annotation.ExceptionDoc;
import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.exception.GlobalCodeException;
import capstone.relation.global.interfaces.SwaggerExampleExceptions;

@ExceptionDoc
public class RefreshAuthExceptionDocs implements SwaggerExampleExceptions {
	@ExplainError("리프레시 토큰이 만료된 경우 발생합니다.")
	public GlobalCodeException 리프레시_토큰_만료 = new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
	@ExplainError("리프레시 토큰이 유효하지 않은 경우 발생합니다.")
	public GlobalCodeException 리프레시_토큰_유효하지_않음 = new AuthException(AuthErrorCode.INVALID_TOKEN);
}
