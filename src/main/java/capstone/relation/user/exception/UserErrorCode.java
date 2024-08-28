package capstone.relation.user.exception;

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
public enum UserErrorCode implements BaseErrorCode {
	INVALID_USER(BAD_REQUEST, "USER_400_1", "유효하지 않은 사용자입니다."),
	INVALID_INVITE_CODE(BAD_REQUEST, "USER_400_2", "유효하지 않은 초대 코드입니다."),
	EXPIRED_INVITE_CODE(BAD_REQUEST, "USER_400_3", "만료된 초대 코드입니다."),
	USER_NOT_FOUND(NOT_FOUND, "USER_404_1", "사용자를 찾을 수 없습니다."),
	USER_ALREADY_EXISTS(CONFLICT, "USER_409_1", "이미 존재하는 사용자입니다."),
	USER_NOT_VERIFIED(UNAUTHORIZED, "USER_401_1", "사용자 인증이 되지 않았습니다."),
	USER_NOT_AUTHORIZED(FORBIDDEN, "USER_403_1", "사용자 권한이 없습니다.");

	private Integer status;
	private String code;
	private String reason;

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
