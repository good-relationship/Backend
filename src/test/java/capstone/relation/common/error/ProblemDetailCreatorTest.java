package capstone.relation.common.error;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

class ProblemDetailCreatorTest {
	@Test
	@DisplayName("create 메서드 테스트")
	void createTest() {
		// Given
		Exception exception = new Exception("Test exception");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/test/uri");

		// When
		ProblemDetail problemDetail = ProblemDetailCreator.create(exception, request, HttpStatus.BAD_REQUEST);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
		assertEquals("Test exception", problemDetail.getDetail());
		assertEquals("/test/uri", problemDetail.getInstance().toString());
		assertEquals("Exception", problemDetail.getTitle());
	}
}