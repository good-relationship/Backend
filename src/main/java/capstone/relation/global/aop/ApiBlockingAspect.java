package capstone.relation.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import capstone.relation.global.exception.GlobalDynamicException;
import capstone.relation.global.helper.SpringEnvironmentHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiBlockingAspect {

	private final SpringEnvironmentHelper springEnvironmentHelper;

	@Around("@annotation(capstone.relation.global.annotation.DevelopOnlyApi)")
	public Object checkApiAcceptingCondition(ProceedingJoinPoint joinPoint) throws Throwable {
		if (springEnvironmentHelper.isProdProfile()) {
			throw new GlobalDynamicException(405, "Blocked Api", "not working api in production");
		}
		return joinPoint.proceed();
	}
}
