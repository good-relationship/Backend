package capstone.relation.websocket.meeting.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.websocket.meeting.dto.IceDto;
import capstone.relation.websocket.meeting.dto.MeetingRoomListDto;
import capstone.relation.websocket.meeting.dto.SdpDto;
import capstone.relation.websocket.meeting.dto.SdpMessageDto;
import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.dto.response.JoinDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Profile("!disabled")  // 활성화되지 않음
@Tag(name = "Meeting", description = "미팅 관련 API (WebSocket) /ws-chat 으로 연결을 수립합니다. "
	+ "클라이언트는 Stomp를 사용하여 이 엔드포인트에 연결할 수 있습니다")
@RestController
@RequestMapping("/ws-chat")
public class MeetingDocumentController {

	@GetMapping("/topic/{workSpaceId}/meetingRoomList")
	@Operation(summary = "현재 워크스페이스에 있는 회의방 목록을 구독", description = "새로운 room이 생기거나 사라지면 해당 메시지를 받습니다."
		+ "실제 구독은 WebSocket 연결 후 STOMP를 통해 이루어집니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 히스토리 수신 성공",
				content = @Content(schema = @Schema(implementation = MeetingRoomListDto.class)))
		}
	)
	public ResponseEntity<MeetingRoomListDto> subscribeMeetingRoomList() {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@PostMapping("/app/room/create")
	@Operation(summary = "회의방 생성", description =
		"새로운 회의방을 생성합니다. 생성된 회의방은 `/topic/{workSpaceId}/meetingRoomList`로 발송됩니다."
			+ "방이 서버에서 생성되면 해당 유저는 /user/queue/join 으로 roomId와 roomName, 참여자 숫자가 와서 해당 방에 join 할 수 있습니다."
			+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다."
	)
	public ResponseEntity<Void> createMeetingRoom(@RequestBody CreateRoomDto createRoomDto) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@PostMapping("/app/room/leave/{roomId}")
	@Operation(summary = "회의방 나가기", description = "회의방에서 나갑니다. 나가면 해당 방의 참여자 수가 줄어듭니다."
		+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다."
	)
	public ResponseEntity<Void> leaveMeetingRoom(@PathVariable String roomId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@GetMapping("/user/queue/join")
	@Operation(summary = "회의방 입장", description = "회의방 입장을 알리는 이벤트 입니다. 방에 입장하면서 해당 방의 정보를 받습니다."
		+ "이 이벤트가 발생하면 회의 방으로 이동시키면 됩니다."
		+ "실제 구독은 WebSocket 연결 후 STOMP를 통해 이루어집니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "메시지 수신 성공",
				content = @Content(schema = @Schema(implementation = JoinDto.class)))
		}
	)
	public ResponseEntity<JoinDto> joinMeetingRoom() {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@PostMapping("/app/room/join/{roomId}")
	@Operation(summary = "방 목록에서 들어가기 요청을 보냅니다.", description = "이것이 성공하면 /user/queue/join이 발생하게 되고, 해당 방에 들어가게 됩니다."
		+ "그 이후 /app/offer/{userId}로 유저 숫자만큼 offer를 보내야 합니다. (그만큼 "
		+ "실제 메시지는 STOMP 프로토콜을 통해 이루어집니다."
	)
	public ResponseEntity<Void> sendJoin(@PathVariable String roomId) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "This endpoint is not implemented.");
	}

	@GetMapping("/topic/offer/{roomId}")
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