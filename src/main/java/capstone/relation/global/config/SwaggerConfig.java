package capstone.relation.global.config;

import static java.util.stream.Collectors.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import capstone.relation.global.annotation.ApiErrorCodeExample;
import capstone.relation.global.annotation.ApiErrorExceptionsExample;
import capstone.relation.global.annotation.ExplainError;
import capstone.relation.global.dto.ErrorReason;
import capstone.relation.global.dto.ErrorResponse;
import capstone.relation.global.exception.BaseErrorCode;
import capstone.relation.global.exception.GlobalCodeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
	private final ApplicationContext applicationContext;

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("조은사이 API Document")
			.version("1.0")
			.description(
				"환영합니다! [조은사이](https://example.com)는 팀프로젝트를 할 때 사용하는 도구를 하나로 모아둔 플랫폼입니다. 이 API 문서는 조은사이의 API를 사용하는 방법을 설명합니다.\n")
			.contact(new io.swagger.v3.oas.models.info.Contact().email("wnddms12345@gmail.com"));

		String jwtScheme = "jwtAuth";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);
		Components components = new Components()
			.addSecuritySchemes(jwtScheme, new SecurityScheme()
				.name("Authorization")
				.type(SecurityScheme.Type.HTTP)
				.in(SecurityScheme.In.HEADER)
				.scheme("Bearer")
				.bearerFormat("JWT"));

		return new OpenAPI()
			.addServersItem(new Server().url("https://joeun.duckdns.org"))
			.addServersItem(new Server().url("http://localhost:8080"))
			.components(new Components())
			.info(info)
			.addSecurityItem(securityRequirement)
			.components(components);
	}

	/**
	 * Swagger 문서의 각 API 동작(Operation)을 사용자 정의하기 위해 사용
	 * ApiErrorCodeExample 어노테이션을 찾습니다. 이 어노테이션은 특정 메서드에 정의된 에러 코드 예시를 설정하는 데 사용됩니다.
	 */
	@Bean
	public OperationCustomizer customize() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			ApiErrorCodeExample apiErrorCodeExample =
				handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
			ApiErrorExceptionsExample apiErrorExceptionsExample =
				handlerMethod.getMethodAnnotation(ApiErrorExceptionsExample.class);
			List<String> tags = getTags(handlerMethod);

			// 태그 중복 설정시 제일 구체적인 값만 태그로 설정
			if (!tags.isEmpty())
				operation.setTags(Collections.singletonList(tags.get(0)));
			// ApiErrorExceptionsExample 어노테이션 단 메소드 적용
			if (apiErrorExceptionsExample != null) {
				generateExceptionResponseExample(operation, apiErrorExceptionsExample.value());
			}
			// ApiErrorCodeExample 어노테이션 단 메소드 적용
			if (apiErrorCodeExample != null)
				generateErrorCodeResponseExample(operation, apiErrorCodeExample.value());

			return operation;
		};
	}

	/**
	 * BaseErrorCode 타입의 이넘값들을 문서화 시킵니다. ExplainError 어노테이션으로 부가설명을 붙일수있습니다. 필드들을 가져와서 예시 에러 객체를
	 * 동적으로 생성해서 예시값으로 붙입니다.
	 */
	private void generateErrorCodeResponseExample(
		Operation operation, Class<? extends BaseErrorCode> type) {
		ApiResponses responses = operation.getResponses();

		BaseErrorCode[] errorCodes = type.getEnumConstants();

		Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
			Arrays.stream(errorCodes)
				.map(
					baseErrorCode -> {
						try {
							ErrorReason errorReason = baseErrorCode.getErrorReason();
							return ExampleHolder.builder()
								.holder(
									getSwaggerExample(
										baseErrorCode.getExplainError(),
										errorReason))
								.code(errorReason.getStatus())
								.name(errorReason.getCode())
								.build();
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(e);
						}
					})
				.collect(groupingBy(ExampleHolder::getCode));

		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	/**
	 * SwaggerExampleExceptions 타입의 클래스를 문서화 시킵니다. SwaggerExampleExceptions 타입의 클래스는 필드로
	 * GlobalCodeException 타입을 가지며, GlobalCodeException 의 errorReason 와,ExplainError 의 설명을
	 * 문서화시킵니다.
	 */
	private void generateExceptionResponseExample(Operation operation, Class<?> type) {
		ApiResponses responses = operation.getResponses();

		// ----------------생성
		Object bean = applicationContext.getBean(type);
		Field[] declaredFields = bean.getClass().getDeclaredFields();
		Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
			Arrays.stream(declaredFields)
				.filter(field -> field.getAnnotation(ExplainError.class) != null)
				.filter(field -> field.getType() == GlobalCodeException.class)
				.map(
					field -> {
						try {
							GlobalCodeException exception =
								(GlobalCodeException)field.get(bean);
							ExplainError annotation =
								field.getAnnotation(ExplainError.class);
							String value = annotation.value();
							ErrorReason errorReason = exception.getErrorReason();
							return ExampleHolder.builder()
								.holder(getSwaggerExample(value, errorReason))
								.code(errorReason.getStatus())
								.name(field.getName())
								.build();
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					})
				.collect(groupingBy(ExampleHolder::getCode));

		// -------------------------- 콘텐츠 세팅 코드별로 진행
		addExamplesToResponses(responses, statusWithExampleHolders);
	}

	/**
	 * : 주어진 ErrorReason을 사용하여 ErrorResponse 객체를 생성합니다. 이 객체는 예시 응답에 포함됩니다.
	 */
	private Example getSwaggerExample(String value, ErrorReason errorReason) {
		ErrorResponse errorResponse = new ErrorResponse(errorReason, "요청시 패스정보입니다.");
		Example example = new Example();
		example.description(value);
		example.setValue(errorResponse);
		return example;
	}

	/**
	 * 상태 코드별로 예시를 API 응답에 추가합니다.
	 */
	private void addExamplesToResponses(
		ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
		statusWithExampleHolders.forEach(
			(status, v) -> {
				Content content = new Content();
				MediaType mediaType = new MediaType();
				ApiResponse apiResponse = new ApiResponse();
				v.forEach(
					exampleHolder -> {
						mediaType.addExamples(
							exampleHolder.getName(), exampleHolder.getHolder());
					});
				content.addMediaType("application/json", mediaType);
				apiResponse.setContent(content);
				responses.addApiResponse(status.toString(), apiResponse);
			});
	}

	/**
	 * API 메서드 및 클래스에 정의된 Tag 어노테이션을 가져와 태그 이름을 추출하고 리스트에 추가 합니다.
	 */
	private static List<String> getTags(HandlerMethod handlerMethod) {
		List<String> tags = new ArrayList<>();

		Tag[] methodTags = handlerMethod.getMethod().getAnnotationsByType(Tag.class);
		List<String> methodTagStrings =
			Arrays.stream(methodTags).map(Tag::name).collect(Collectors.toList());

		Tag[] classTags = handlerMethod.getClass().getAnnotationsByType(Tag.class);
		List<String> classTagStrings =
			Arrays.stream(classTags).map(Tag::name).collect(Collectors.toList());
		tags.addAll(methodTagStrings);
		tags.addAll(classTagStrings);
		return tags;
	}
}

