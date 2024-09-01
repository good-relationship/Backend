package capstone.relation.api.auth.docs;

import capstone.relation.global.annotation.ExceptionDoc;
import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.exception.GlobalCodeException;
import capstone.relation.global.exception.GlobalErrorCode;
import capstone.relation.global.exception.GlobalException;
import capstone.relation.global.interfaces.SwaggerExampleExceptions;

@ExceptionDoc
public class LoginAuthExceptionDocs implements SwaggerExampleExceptions {
	@ExplainError("로그인 서버에 보내는 요청에 실패한 경우 발생합니다.")
	public GlobalCodeException 서버_요청_오류 = new GlobalException(GlobalErrorCode.OTHER_SERVER_BAD_REQUEST);

	@ExplainError("카카오 서버에 보낸 토큰이 만료된 경우 발생합니다.")
	public GlobalCodeException 서버_토큰_만료 = new GlobalException(GlobalErrorCode.OTHER_SERVER_EXPIRED_TOKEN);
}
