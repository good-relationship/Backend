package capstone.relation.websocket.meeting.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.websocket.meeting.dto.IceDto;
import capstone.relation.websocket.meeting.dto.SdpDto;
import capstone.relation.websocket.meeting.dto.SdpMessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Profile("!disabled")  // 활성화되지 않음
@Tag(name = "Meet Connection", description = "미팅 관련 API (WebSocket) /ws-chat 으로 연결을 수립합니다. "
	+ "클라이언트는 Stomp를 사용하여 이 엔드포인트에 연결할 수 있습니다")
@RestController
@RequestMapping("/ws-chat")
public class SignalingDocumentController {
	@GetMapping("/user/queue/offer/{roomId}")
	@Operation(summary = "새로운 offer 가 왔는지 구독", description =
		"현재 들어가 있는 회의방의 roomId를 기반으로 구독하며 해당 메시지를 받으면 `/app/answer`로 상대방 userId와 내 description(sdp)을 함께 발송합니다. "
			+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 수신 성공",
				content = @Content(schema = @Schema(implementation = SdpMessageDto.class)))
		}
	)
	public ResponseEntity<SdpMessageDto> subScribeOffer(@PathVariable String roomId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@PostMapping("/app/offer/{roomId}")
	@Operation(summary = "상대방에게 offer를 보냅니다.", description = "상대방에게 offer를 보냅니다. 이건 Room에 Join하면 보냅니다."
		+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다."
	)
	public ResponseEntity<Void> sendOffer(@PathVariable String roomId, SdpDto sdpDto) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@GetMapping("/user/queue/answer")
	@Operation(summary = "상대방의 answer를 구독", description = "상대방이 내 offer에 대한 answer를 보내면 해당 메시지를 받습니다."
		+ "실제 구독은 WebSocket 연결 후 STOMP를 통해 이루어집니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 수신 성공",
				content = @Content(schema = @Schema(implementation = SdpMessageDto.class)))
		}
	)
	public ResponseEntity<SdpMessageDto> subscribeAnswer() {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@PostMapping("/app/answer")
	@Operation(summary = "상대방에게 answer를 보냅니다.", description = "상대방의 offer에 대한 answer를 보냅니다."
		+ "userId 는 상대방 id입니다."
		+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다."
	)
	public ResponseEntity<SdpMessageDto> sendAnswer() {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@GetMapping("/topic/ice/{roomId}")
	@Operation(summary = "상대방의 ice candidate를 구독", description = "상대방이 내 offer에 대한 ice candidate를 보내면 해당 메시지를 받습니다."
		+ "실제 구독은 WebSocket 연결 후 STOMP를 통해 이루어집니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 수신 성공",
				content = @Content(schema = @Schema(implementation = IceDto.class)))
		}
	)
	public ResponseEntity<IceDto> subscribeIce(@PathVariable String roomId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@PostMapping("/app/ice/{roomId}")
	@Operation(summary = "상대방에게 ice candidate를 보냅니다.", description = "상대방에게 ice candidate를 보냅니다."
		+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다."
	)
	public ResponseEntity<Void> sendIce(@PathVariable String roomId, IceDto iceDto) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

}
