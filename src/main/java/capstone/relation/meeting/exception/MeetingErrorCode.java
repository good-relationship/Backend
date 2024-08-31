package capstone.relation.meeting.exception;

import static capstone.relation.global.consts.JoEunStatic.*;

import java.lang.reflect.Field;
import java.util.Objects;

import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.dto.ErrorReason;
import capstone.relation.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingErrorCode implements BaseErrorCode {
	@ExplainError("해당 회의가 존재하지 않는 경우에 발생합니다.")
	INVALID_MEETING(NOT_FOUND, "MEETING_404_1", "해당 회의가 존재하지 않습니다."),
	@ExplainError("해당 회의에 가입하지 않은 사용자 입니다.")
	USER_NOT_MEETING_MEMBER(403, "MEETING_403_1", "해당 회의에 가입하지 않은 사용자 입니다."),
	@ExplainError("회의 참여에 인원 초과로 실패한 경우 발생합니다.")
	MEETING_JOIN_FAILED(409, "MEETING_409_1", "회의 참여에 실패했습니다.(인원 초과)"),
	@ExplainError("회의에 이미 참여해 있는 경우 발생합니다.")
	MEETING_ALREADY_JOINED(409, "MEETING_409_2", "이미 회의에 참여해 있습니다."),
	@ExplainError("회의 생성에 실패한 경우에 발생합니다.")
	MEETING_CREATE_FAILED(500, "MEETING_500_1", "회의 생성에 실패했습니다."),
	@ExplainError("회의 나가기에 실패한 경우에 발생합니다.")
	MEETING_LEAVE_FAILED(500, "MEETING_500_2", "회의 나가기에 실패했습니다.");

	private final Integer status;
	private final String code;
	private final String reason;

	@Override
	public ErrorReason getErrorReason() {
		return ErrorReason.builder().reason(reason).code(code).status(status).build();
	}

	@Override
	public String getExplainError() throws NoSuchFieldException {
		Field field = this.getClass().getField(this.name());
		ExplainError annotation = field.getAnnotation(ExplainError.class);
		return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
	}
}
