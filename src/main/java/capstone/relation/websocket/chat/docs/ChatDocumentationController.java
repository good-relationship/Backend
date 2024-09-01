package capstone.relation.websocket.chat.docs;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.global.annotation.ApiErrorExceptionsExample;
import capstone.relation.websocket.chat.dto.publish.HistoryPublishDto;
import capstone.relation.websocket.chat.dto.publish.MessagePublishDto;
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.workspace.docs.WorkspaceGetExceptionDocs;
import capstone.relation.workspace.docs.WorkspaceWithIdExceptionDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Profile("!disabled")  // 활성화되지 않음
@Tag(name = "Chat", description = "채팅 관련 API (WebSocket) /ws-chat 으로 연결을 수립합니다. "
	+ "클라이언트는 Stomp를 사용하여 이 엔드포인트에 연결할 수 있습니다")
@RestController
@RequestMapping("/ws-chat")
public class ChatDocumentationController {

	//TODO: WebSocket API exception 문서화
	@GetMapping("/topic/message/{workSpaceId}")
	@Operation(summary = "새 메시지 구독", description = "워크스페이스 ID를 지정하여 새 메시지를 `/topic/message`로 발송합니다. "
		+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 수신 성공",
				content = @Content(schema = @Schema(implementation = MessageDto.class)))
		}
	)
	public ResponseEntity<MessageDto> subscribeMessage(@PathVariable String workSpaceId) {
		throw new UnsupportedOperationException("This endpoint is not implemented.");
	}

	@GetMapping("/user/queue/history")
	@Operation(summary = "메시지 히스토리 구독", description = "/topic/history를 구독하여 이전 메시지 히스토리를 수신합니다. "
		+ "실제 구독은 WebSocket 연결 후 STOMP를 통해 이루어집니다. 메시지는 10개 단위로 옵니다(페이징).",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 히스토리 수신 성공",
				content = @Content(schema = @Schema(implementation = HistoryResponseDto.class)))
		}
	)
	public ResponseEntity<HistoryResponseDto> subscribeHistory() {
		throw new UnsupportedOperationException("This endpoint is not implemented.");
	}

	@PostMapping("/app/message/{workSpaceId}")
	@Operation(summary = "메시지 발송", description = "새 메시지를 `/app/message` 경로로 발송합니다.")
	@ApiErrorExceptionsExample(WorkspaceWithIdExceptionDocs.class)
	public ResponseEntity<Void> sendMessage(@PathVariable String workSpaceId, MessagePublishDto message) {
		throw new UnsupportedOperationException("This endpoint is not implemented.");
	}

	@PostMapping("/app/history")
	@Operation(summary = "메시지 히스토리 구독", description = "특정 사용자의 메시지 히스토리를 요청 합니다. "
		+ "메시지 히스토리는 STOMP 프로토콜을 통해 `/topic/history`로 전송됩니다.",
		responses = {
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "400", description = "Bad Request")
		}
	)
	@ApiErrorExceptionsExample(WorkspaceGetExceptionDocs.class)
	public ResponseEntity<Void> requestHistory(@RequestBody HistoryPublishDto historyPublishDto) {
		throw new UnsupportedOperationException("This endpoint is not implemented.");
	}
}
