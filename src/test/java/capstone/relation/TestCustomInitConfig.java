package capstone.relation;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import capstone.relation.common.initializer.CustomInitListener;
import capstone.relation.workspace.school.service.SchoolService;

@TestConfiguration
@Profile("test")
public class TestCustomInitConfig {

	@Bean
	public CustomInitListener customInitListener(SchoolService schoolService) {
		return new CustomInitListener(schoolService);
	}
}
