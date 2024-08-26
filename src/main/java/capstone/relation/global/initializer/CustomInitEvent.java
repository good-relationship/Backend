package capstone.relation.global.initializer;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Profile;

@Profile("dev")
public class CustomInitEvent extends ApplicationEvent {
	// 여기에 추가 데이터 필드나 메서드를 정의할 수 있습니다.

	public CustomInitEvent(Object source) {
		super(source);
	}
}
