package capstone.relation.global.exception;

import capstone.relation.global.dto.ErrorReason;

public interface BaseErrorCode {
	public ErrorReason getErrorReason();

	String getExplainError() throws NoSuchFieldException;
}
