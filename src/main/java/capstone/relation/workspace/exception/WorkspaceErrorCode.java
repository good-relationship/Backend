package capstone.relation.workspace.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum WorkspaceErrorCode {
	INVALID_ACCESS(HttpStatus.BAD_REQUEST, "해당 워크스페이스에 접근 권한이 없습니다."),
	INVALID_WORKSPACE(HttpStatus.BAD_REQUEST, "해당 워크스페이스가 존재하지 않습니다."),
	INVALID_WORKSPACE_JOIN(HttpStatus.BAD_REQUEST, "해당 워크스페이스에 가입하지 않았습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	WorkspaceErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}