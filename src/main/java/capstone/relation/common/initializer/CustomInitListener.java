package capstone.relation.common.initializer;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import capstone.relation.workspace.school.service.SchoolService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomInitListener {

	private final SchoolService schoolService;

	@EventListener
	public void onCustomInit(CustomInitEvent event) {
		schoolService.initSchool();
	}
}
