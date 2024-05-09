package capstone.relation.websocket.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ICE Candidate를 받는 DTO")
public class IceDto {
	@Schema(description = "메시지를 보낸 사용자 ID")
	private SdpDto sdp;
	@Schema(description = "sdpMLineIndex 값입니다.", example = "0")
	private String sdpMLineIndex;
	@Schema(description = "sdpMid 값입니다.", example = "0")
	private String sdpMid;
	@Schema(description = "candidate", example = "candidate:2498956781 1 udp 2122260223 10.19.222.94 54366"
		+ " typ host generation 0 ufrag 3Zf2 network-id 1 network-cost 10")
	private String candidate;
}
