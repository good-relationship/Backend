package capstone.relation.common.error;

import org.springframework.validation.FieldError;

import lombok.Builder;

@Builder
public record ValidationError(
	String field,
	String message
) {
	public static ValidationError of(FieldError fieldError) {
		return ValidationError.builder()
			.field(fieldError.getField())
			.message(fieldError.getDefaultMessage())
			.build();
	}
}
