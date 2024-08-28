package capstone.relation.api.auth.exception;

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
public enum AuthErrorCode implements BaseErrorCode {

	@ExplainError("accessToken 만료시 발생하는 오류입니다.")
	TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_1", "인증 시간이 만료되었습니다. 인증토큰을 재 발급 해주세요"),

	@ExplainError("refreshToken 만료시 발생하는 오류입니다.")
	REFRESH_TOKEN_EXPIRED(FORBIDDEN, "AUTH_403_1", "인증 시간이 만료되었습니다. 재 로그인 해주세요."),
	@ExplainError("헤더에 올바른 accessToken을 담지않았을 때 발생하는 오류(형식 불일치 등)")
	ACCESS_TOKEN_NOT_EXIST(FORBIDDEN, "AUTH_403_2", "알맞은 accessToken을 넣어주세요."),
	@ExplainError("인증 토큰이 잘못됐을 때 발생하는 오류입니다.")
	INVALID_TOKEN(UNAUTHORIZED, "GLOBAL_401_1", "잘못된 토큰입니다. 재 로그인 해주세요");

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
