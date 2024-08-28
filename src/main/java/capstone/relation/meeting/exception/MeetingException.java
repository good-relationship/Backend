package capstone.relation.meeting.exception;

import capstone.relation.global.exception.GlobalCodeException;

public class MeetingException extends GlobalCodeException {
	public MeetingException(MeetingErrorCode errorCode) {
		super(errorCode);
	}
}
