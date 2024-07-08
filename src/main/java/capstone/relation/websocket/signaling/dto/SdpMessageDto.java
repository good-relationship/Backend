package capstone.relation.websocket.signaling.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "offer 또는 answer를 보낼 때 사용하는 DTO")
public class SdpMessageDto {
	@Schema(description = "메시지를 받을 사용자(즉 자신이 아닌 통신하고 있는 유저 id)", example = "123")
	private String userId;
	@Schema(description = "offer description 즉 createOffer 함수 이벤트 description 값을 그대로 여기 넣어서 보내면 됩니다.")
	private SdpDto sessionDescription;

	@Schema(description = "메시지 타입 \n"
		+ "ScreenShare | Video", example = "SceenShare")
	private SignalMessageType type;
}
