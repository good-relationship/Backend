package capstone.relation.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import capstone.relation.global.interfaces.SwaggerExampleExceptions;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorExceptionsExample {
	Class<? extends SwaggerExampleExceptions> value();
}