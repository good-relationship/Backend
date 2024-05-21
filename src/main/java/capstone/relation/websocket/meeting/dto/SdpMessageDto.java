package capstone.relation.websocket.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "offer 또는 answer를 받는 DTO")
public class SdpMessageDto {
	@Schema(description = "메시지를 보낸 사용자 ID 또는 받을 사용자(즉 자신이 아닌 통신하고 있는 유저 id)", example = "123")
	private String userId;
	@Schema(description = "offer description 즉 createOffer 함수 이벤트 description 값을 그대로 여기 넣어서 보내면 됩니다.")
	private SdpDto sessionDescription;
}
