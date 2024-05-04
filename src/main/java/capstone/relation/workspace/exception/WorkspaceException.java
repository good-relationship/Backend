package capstone.relation.workspace.exception;

import lombok.Getter;

@Getter
public class WorkspaceException extends RuntimeException {
	private final WorkspaceErrorCode errorCode;

	public WorkspaceException(WorkspaceErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public WorkspaceException(WorkspaceErrorCode errorCode, String message) {
		super(errorCode.getMessage() + " : " + message);
		this.errorCode = errorCode;
	}

}
