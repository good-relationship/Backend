package capstone.relation.workspace.exception;

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
public enum WorkSpaceErrorCode implements BaseErrorCode {
	@ExplainError("유저가 가입한 워크스페이스가 없을 때 발생합니다.")
	NO_WORKSPACE(404, "WORKSPACE_404_1", "가입한 워크스페이스가 없습니다."),
	@ExplainError("해당 워크스페이스가 가입은 되어 있으나 접근 권한이 없는 경우에 발생합니다.")
	INVALID_ACCESS(FORBIDDEN, "WORKSPACE_403_1", "해당 워크스페이스에 접근 권한이 없습니다."),
	@ExplainError("해당 워크스페이스가 존재하지 않는 경우에 발생합니다. 초대 이후에 삭제된 경우 등")
	INVALID_WORKSPACE(NOT_FOUND, "WORKSPACE_404_2", "해당 워크스페이스가 존재하지 않습니다."),
	@ExplainError("해당 워크스페이스에 가입하지 않은 사용자 입니다.")
	USER_NOT_WORKSPACE_MEMBER(403, "WORKSPACE_403_1", "해당 워크스페이스에 가입하지 않은 사용자 입니다.");

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
