package capstone.relation.workspace.exception;

import capstone.relation.global.exception.GlobalCodeException;

public class WorkSpaceException extends GlobalCodeException {
	public WorkSpaceException(WorkSpaceErrorCode errorCode) {
		super(errorCode);
	}
}
