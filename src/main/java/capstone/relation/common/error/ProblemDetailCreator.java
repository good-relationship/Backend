package capstone.relation.common.error;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;

public class ProblemDetailCreator {
	private ProblemDetailCreator() {
	}

	public static ProblemDetail create(Exception ex, HttpServletRequest request, HttpStatus status) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
		problemDetail.setInstance(URI.create(request.getRequestURI()));
		problemDetail.setTitle(ex.getClass().getSimpleName());
		return problemDetail;
	}

	public static ProblemDetail createValidationDetails(MethodArgumentNotValidException ex, HttpServletRequest request,
		HttpStatus status) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
		problemDetail.setInstance(URI.create(request.getRequestURI()));
		problemDetail.setTitle(ex.getClass().getSimpleName());

		problemDetail.setProperty("validationError", ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(ValidationError::of)
			.toList());
		return problemDetail;
	}
}
