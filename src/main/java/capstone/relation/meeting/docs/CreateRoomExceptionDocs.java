package capstone.relation.meeting.docs;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.global.annotation.ExceptionDoc;
import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.exception.GlobalCodeException;
import capstone.relation.global.interfaces.SwaggerExampleExceptions;
import capstone.relation.meeting.exception.MeetingErrorCode;
import capstone.relation.meeting.exception.MeetingException;

//TODO: Auth 관련된 녀석들이 공통 되는 부분이 많으니까 그거에 맞게 수정해야할 듯
@ExceptionDoc
public class CreateRoomExceptionDocs implements SwaggerExampleExceptions {

	@ExplainError("회의실을 생성하거나 참여하고자 할 때 이미 회의실에 참여되어있는 경우 발생합니다.")
	public GlobalCodeException 회의실_참여_실패 = new MeetingException(MeetingErrorCode.MEETING_ALREADY_JOINED);
	@ExplainError("회의실 이름이 없는 경우 발생합니다.")
	public GlobalCodeException 회의실_이름_없음 = new MeetingException(MeetingErrorCode.MEETING_NAME_NOT_EXIST);
	@ExplainError("엑세스 토큰이 만료된 경우 발생합니다.")
	public GlobalCodeException 토큰_만료 = new AuthException(AuthErrorCode.TOKEN_EXPIRED);
	@ExplainError("엑세스 토큰이 유효하지 않은 경우 발생합니다.")
	public GlobalCodeException 토큰_유효하지_않음 = new AuthException(AuthErrorCode.INVALID_TOKEN);
	@ExplainError("엑세스 토큰이 없는 경우 발생합니다.")
	public GlobalCodeException 토큰_없음 = new AuthException(AuthErrorCode.ACCESS_TOKEN_NOT_EXIST);
}
