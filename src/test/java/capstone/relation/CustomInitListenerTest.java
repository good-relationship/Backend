package capstone.relation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import capstone.relation.workspace.school.service.SchoolService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestCustomInitConfig.class}) // 테스트 전용 설정 클래스 추가
@ActiveProfiles("test")
public class CustomInitListenerTest {

	@Autowired
	private SchoolService schoolService;

	@Test
	public void testCustomInitListener() {
		// 테스트 로직
		// CustomInitListener가 호출되어 schoolService.initSchool() 메서드가 실행되는지 확인
		schoolService.initSchool();
	}
}
