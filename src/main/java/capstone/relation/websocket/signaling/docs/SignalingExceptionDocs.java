package capstone.relation.websocket.signaling.docs;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.global.annotation.ExceptionDoc;
import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.exception.GlobalCodeException;
import capstone.relation.global.interfaces.SwaggerExampleExceptions;
import capstone.relation.meeting.exception.MeetingErrorCode;
import capstone.relation.meeting.exception.MeetingException;
import capstone.relation.user.exception.UserErrorCode;
import capstone.relation.user.exception.UserException;

@ExceptionDoc
public class SignalingExceptionDocs implements SwaggerExampleExceptions {
	@ExplainError("유저 정보를 찾을 수 없는 경우 발생합니다.(유저의 탈퇴 등으로 못찾는 경우 또는 가입이 되어있지 않는 경우)")
	public GlobalCodeException 유저_정보_없음 = new UserException(UserErrorCode.USER_NOT_FOUND);
	@ExplainError("해당 하는 회의가 없습니다.")
	public GlobalCodeException 회의_없음 = new MeetingException(MeetingErrorCode.INVALID_MEETING);
	@ExplainError("해당 회의에 가입하지 않은 사용자 입니다.")
	public GlobalCodeException 회의_가입_안됨 = new MeetingException(MeetingErrorCode.USER_NOT_MEETING_MEMBER);
	@ExplainError("SDP 정보에서 유저 ID가 잘못됐습니다.")
	public GlobalCodeException SDP_유저_ID_잘못됨 = new UserException(UserErrorCode.INVALID_USER);
	@ExplainError("엑세스 토큰이 만료된 경우 발생합니다.")
	public GlobalCodeException 토큰_만료 = new AuthException(AuthErrorCode.TOKEN_EXPIRED);
	@ExplainError("엑세스 토큰이 유효하지 않은 경우 발생합니다.")
	public GlobalCodeException 토큰_유효하지_않음 = new AuthException(AuthErrorCode.INVALID_TOKEN);
	@ExplainError("엑세스 토큰이 없는 경우 발생합니다.")
	public GlobalCodeException 토큰_없음 = new AuthException(AuthErrorCode.ACCESS_TOKEN_NOT_EXIST);
}

