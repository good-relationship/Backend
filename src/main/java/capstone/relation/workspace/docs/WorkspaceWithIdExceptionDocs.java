package capstone.relation.workspace.docs;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.global.annotation.ExceptionDoc;
import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.exception.GlobalCodeException;
import capstone.relation.global.interfaces.SwaggerExampleExceptions;
import capstone.relation.workspace.exception.WorkSpaceErrorCode;
import capstone.relation.workspace.exception.WorkSpaceException;

@ExceptionDoc
public class WorkspaceWithIdExceptionDocs implements SwaggerExampleExceptions {
	@ExplainError("유저가 Workspace 에 참여해 있지 않은 경우 발생합니다.")
	public GlobalCodeException 워크스페이스_없음 = new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE);

	@ExplainError("해당 Workspace 에 참여해 있지 않은 경우 발생합니다.")
	public GlobalCodeException 워크스페이스_멤버아님 = new WorkSpaceException(WorkSpaceErrorCode.USER_NOT_WORKSPACE_MEMBER);

	@ExplainError("해당 Workspace 가 존재하지 않는 경우 발생합니다.")
	public GlobalCodeException 워크스페이스_없음_2 = new WorkSpaceException(WorkSpaceErrorCode.INVALID_WORKSPACE);
	@ExplainError("엑세스 토큰이 만료된 경우 발생합니다.")
	public GlobalCodeException 토큰_만료 = new AuthException(AuthErrorCode.TOKEN_EXPIRED);
	@ExplainError("엑세스 토큰이 유효하지 않은 경우 발생합니다.")
	public GlobalCodeException 토큰_유효하지_않음 = new AuthException(AuthErrorCode.INVALID_TOKEN);
	@ExplainError("엑세스 토큰이 없는 경우 발생합니다.")
	public GlobalCodeException 토큰_없음 = new AuthException(AuthErrorCode.ACCESS_TOKEN_NOT_EXIST);
}
