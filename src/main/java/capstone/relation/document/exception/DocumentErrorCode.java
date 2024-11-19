package capstone.relation.document.exception;

import java.lang.reflect.Field;
import java.util.Objects;

import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.dto.ErrorReason;
import capstone.relation.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentErrorCode implements BaseErrorCode {
	@ExplainError("폴더 아이디가 잘못되었을 때 발생하는 오류입니다.")
	FOLDER_NOT_EXIST(404, "DOC_404_1", "폴더 아이디가 잘못되었습니다."),
	@ExplainError("파일 아이디가 잘못되었을 때 발생하는 오류입니다.")
	FILE_NOT_EXIST(404, "DOC_404_2", "파일 아이디가 잘못되었습니다."),
	@ExplainError("유저가 접근 할 수 없는 워크스페이스에 접근하려고 할 때 발생하는 오류입니다.")
	USER_NOT_ACCESS(403, "DOC_403_1", "유저가 접근 할 수 없는 워크스페이스에 접근하려고 합니다.");

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
