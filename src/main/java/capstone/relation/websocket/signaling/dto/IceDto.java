package capstone.relation.websocket.signaling.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ICE Candidate를 받는 DTO")
public class IceDto {
	@Schema(description = "sdpMLineIndex 값입니다.", example = "0")
	private String sdpMLineIndex;
	@Schema(description = "sdpMid 값입니다.", example = "0")
	private String sdpMid;
	@Schema(description = "candidate", example = "candidate:2498956781 1 udp 2122260223 10.19.222.94 54366"
		+ " typ host generation 0 ufrag 3Zf2 network-id 1 network-cost 10")
	private String candidate;
	@Schema(description = "상대방 ID", example = "1")
	private String userId;

	@Schema(description = "메시지 타입 \n"
		+ "ScreenShare | Video", example = "ScreenShare")
	private SignalMessageType type;
}
