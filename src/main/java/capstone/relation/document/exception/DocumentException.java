package capstone.relation.document.exception;

import capstone.relation.global.exception.GlobalCodeException;
import lombok.Getter;

@Getter
public class DocumentException extends GlobalCodeException {

	public DocumentException(DocumentErrorCode errorCode) {
		super(errorCode);
	}
}
