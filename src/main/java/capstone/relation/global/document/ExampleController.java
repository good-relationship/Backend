package capstone.relation.global.document;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.global.annotation.ApiErrorCodeExample;
import capstone.relation.global.annotation.DevelopOnlyApi;
import capstone.relation.global.exception.GlobalErrorCode;
import capstone.relation.meeting.exception.MeetingErrorCode;
import capstone.relation.user.exception.UserErrorCode;
import capstone.relation.workspace.exception.WorkSpaceErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/example")
@RequiredArgsConstructor
@Tag(name = "Exception Document", description = "예제 에러코드 문서화")
public class ExampleController {
	@GetMapping("/global")
	@DevelopOnlyApi
	@ApiErrorCodeExample(GlobalErrorCode.class)
	@Operation(summary = "글로벌 (aop, 서버 내부 오류등)  관련 에러 코드 나열")
	public void example() {
	}

	@GetMapping("/auth")
	@DevelopOnlyApi
	@Operation(summary = "인증 도메인 관련 에러 코드 나열")
	@ApiErrorCodeExample(AuthErrorCode.class)
	public void getAuthErrorCode() {
	}

	@GetMapping("/user")
	@DevelopOnlyApi
	@Operation(summary = "유저 도메인 관련 에러 코드 나열")
	@ApiErrorCodeExample(UserErrorCode.class)
	public void getUserErrorCode() {
	}

	@GetMapping("/workspace")
	@DevelopOnlyApi
	@Operation(summary = "워크스페이스 도메인 관련 에러 코드 나열")
	@ApiErrorCodeExample(WorkSpaceErrorCode.class)
	public void getWorkSpaceErrorCode() {
	}

	@GetMapping("/meeting")
	@DevelopOnlyApi
	@Operation(summary = "화상회의 도메인 관련 에러 코드 나열")
	@ApiErrorCodeExample(MeetingErrorCode.class)
	public void getMeetingErrorCode() {
	}

	// @GetMapping("/chat")
	// @DevelopOnlyApi
	// @Operation(summary = "채팅 도메인 관련 에러 코드 나열 (이건 소켓으로 처리됩니다.")
	// @ApiErrorCodeExample(ChatErrorCode.class)
	// public void getChatErrorCode() {
	// }

	// @GetMapping("/socket")
	// @DevelopOnlyApi
	// @Operation(summary = "소켓 도메인 관련 에러 코드 나열")
	// @ApiErrorCodeExample(SocketErrorCode.class)
	// public void getSocketErrorCode() {
	// }
}
