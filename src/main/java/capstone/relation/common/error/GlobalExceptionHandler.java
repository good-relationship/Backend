package capstone.relation.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class) //커스텀 예외처리
	public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
		return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleAllException(Exception e, HttpServletRequest request) {
		ProblemDetail problemDetail = ProblemDetailCreator.create(e, request, HttpStatus.BAD_REQUEST);

		return ResponseEntity.badRequest().body(problemDetail);
	}
}
