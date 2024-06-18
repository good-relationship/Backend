package capstone.relation.websocket.signaling.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SdpDto {
	@Schema(description = "offer 또는 answer", example = "offer")
	private String type;
	@Schema(description = "sdp", example =
		"v=0\r\no=- 123456 123456 IN IP4 127. 0. 0. 1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE data\r\n"
			+ "a=msid-semantic: WMS\r\nm=application 9 UDP/DTLS/SCTP webrtc-datachannel\r\nc=IN IP4")
	private String sdp;
}
