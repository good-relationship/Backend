package capstone.relation.global.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import capstone.relation.global.dto.ErrorReason;
import capstone.relation.global.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	//TODO: 디코 알림 보내는 거 추가

	// ResponseEntityExceptionHandler를 상속받아서 예외처리를 할 수 있다.
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ServletWebRequest servletWebRequest = (ServletWebRequest)request;
		HttpServletRequest httpServletRequest = servletWebRequest.getRequest(); // 예외가 발생한 URL과 같은 요청에 대한 세부 정보를 추출
		String url = httpServletRequest.getRequestURL().toString();

		HttpStatus httpStatus = (HttpStatus)status;
		ErrorResponse errorResponse =
			new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), ex.getMessage(),
				url); // 사용자 정의 ErrorResponse 객체를 생성
		return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
	}

	//주로 요청 본문이 유효성 검사를 통과하지 못할 때 발생합니다 (예: @Valid 어노테이션 사용 시) MethodArgumentNotValidException 예외를 처리하는 메서드
	@SneakyThrows // 메서드 선언부에 Throws 를 정의하지 않고도, 검사 된 예외를 Throw 할 수 있도록 하는 Lombok 에서 제공하는 어노테이션입
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request) {

		List<FieldError> errors = ex.getBindingResult().getFieldErrors();
		ServletWebRequest servletWebRequest = (ServletWebRequest)request;
		HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
		String url = httpServletRequest.getRequestURL().toString();

		Map<String, Object> fieldAndErrorMessages =
			errors.stream()
				.collect(
					Collectors.toMap(
						FieldError::getField, FieldError::getDefaultMessage));

		String errorsToJsonString = new ObjectMapper().writeValueAsString(fieldAndErrorMessages);

		HttpStatus httpStatus = (HttpStatus)status;
		ErrorResponse errorResponse =
			new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), errorsToJsonString, url);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/** Request Param Validation 예외 처리
	 * 유효성 검사 제약 조건이 위반되었을 때 발생합니다. (예: @NotNull, @Size, @Email 어노테이션 사용 시)
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> constraintViolationExceptionHandler(
		ConstraintViolationException ex, HttpServletRequest request) {
		Map<String, Object> bindingErrors = new HashMap<>(); // 유효성 검사 실패 필드와 해당 오류 메시지를 저장하기 위한 맵을 생성
		// 예외 객체에서 유효성 검사 위반 항목들을 가져옴.
		ex.getConstraintViolations()
			.forEach(
				constraintViolation -> {
					//위반된 속성의 경로를 가져옵니다. 이 경로는 문자열로 변환되어 점(.)을 기준으로 분할됩니다
					List<String> propertyPath =
						List.of(
							constraintViolation
								.getPropertyPath()
								.toString()
								.split("\\."));
					// 마지막 요소를 추출하여 실제 필드 이름을 가져옵니다. 예를 들어, 경로가 user.address.street라면 street가 추출됩니다.
					String path =
						propertyPath.stream()
							.skip(propertyPath.size() - 1L)
							.findFirst()
							.orElse(null);
					//위반된 필드 이름과 해당 오류 메시지를 맵에 저장
					bindingErrors.put(path, constraintViolation.getMessage());
				});

		ErrorReason errorReason =
			ErrorReason.builder()
				.code("BAD_REQUEST")
				.status(400)
				.reason(bindingErrors.toString())
				.build();
		ErrorResponse errorResponse =
			new ErrorResponse(errorReason, request.getRequestURL().toString());
		return ResponseEntity.status(HttpStatus.valueOf(errorReason.getStatus()))
			.body(errorResponse);
	}

	@ExceptionHandler(GlobalCodeException.class)
	public ResponseEntity<ErrorResponse> joEunCodeExceptionHandler(
		GlobalCodeException e, HttpServletRequest request) {
		BaseErrorCode code = e.getErrorCode();
		ErrorReason errorReason = code.getErrorReason();
		ErrorResponse errorResponse =
			new ErrorResponse(errorReason, request.getRequestURL().toString());
		return ResponseEntity.status(HttpStatus.valueOf(errorReason.getStatus()))
			.body(errorResponse);
	}

	@ExceptionHandler(GlobalDynamicException.class)
	public ResponseEntity<ErrorResponse> joEunDynamicExceptionHandler(
		GlobalDynamicException e, HttpServletRequest request) {
		ErrorResponse errorResponse =
			new ErrorResponse(
				e.getStatus(),
				e.getCode(),
				e.getReason(),
				request.getRequestURL().toString());
		return ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(errorResponse);
	}

	//TODO: 이 경우 디코에 알림 가도록 구성해도 좋겠다.
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request)
		throws IOException {
		ServletWebRequest servletWebRequest = new ServletWebRequest(request);
		HttpServletRequest httpServletRequest = servletWebRequest.getRequest(); // 예외가 발생한 URL과 같은 요청에 대한 세부 정보를 추출
		String url = httpServletRequest.getRequestURL().toString();

		log.error("INTERNAL_SERVER_ERROR", ex);
		GlobalErrorCode internalServerError = GlobalErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse =
			new ErrorResponse(
				internalServerError.getStatus(),
				internalServerError.getCode(),
				internalServerError.getReason(),
				url);

		return ResponseEntity.status(HttpStatus.valueOf(internalServerError.getStatus()))
			.body(errorResponse);
	}
}
