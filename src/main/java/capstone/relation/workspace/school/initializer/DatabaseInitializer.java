package capstone.relation.workspace.school.initializer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// 이벤트 발행
		eventPublisher.publishEvent(new CustomInitEvent(this));
	}
}

