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
public class WorkspaceInviteExceptionDocs implements SwaggerExampleExceptions {
	@ExplainError("유저가 이미 Workspace 에 참여해 있는 경우 발생합니다.")
	public GlobalCodeException 워크스페이스_이미_참여 = new WorkSpaceException(WorkSpaceErrorCode.ALREADY_WORKSPACE_MEMBER);
	@ExplainError("초대 코드가 만료되었을 때 발생합니다.")
	public GlobalCodeException 초대_만료 = new WorkSpaceException(WorkSpaceErrorCode.EXPIRED_INVITE_CODE);
	@ExplainError("초대 코드가 유효하지 않을 때 발생합니다.")
	public GlobalCodeException 초대_유효하지_않음 = new WorkSpaceException(WorkSpaceErrorCode.INVALID_INVITE_CODE);
	@ExplainError("엑세스 토큰이 만료된 경우 발생합니다.")
	public GlobalCodeException 토큰_만료 = new AuthException(AuthErrorCode.TOKEN_EXPIRED);
	@ExplainError("엑세스 토큰이 유효하지 않은 경우 발생합니다.")
	public GlobalCodeException 토큰_유효하지_않음 = new AuthException(AuthErrorCode.INVALID_TOKEN);
	@ExplainError("엑세스 토큰이 없는 경우 발생합니다.")
	public GlobalCodeException 토큰_없음 = new AuthException(AuthErrorCode.ACCESS_TOKEN_NOT_EXIST);
}
