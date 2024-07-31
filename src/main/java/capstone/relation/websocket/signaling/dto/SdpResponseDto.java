package capstone.relation.websocket.signaling.dto;

import capstone.relation.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "offer 또는 answer를 받을 때 사용하는 DTO")
public class SdpResponseDto {
	@Schema(description = "메시지를 보낸 사용자 정보(즉 자신이 아닌 통신하고 있는 유저 id)")
	private UserInfoDto userInfo;
	@Schema(description = "offer description 즉 createOffer 함수 이벤트 description 값을 그대로 여기 넣어서 보내면 됩니다.")
	private SdpDto sessionDescription;

	@Schema(description = "메시지 타입 \n"
		+ "ScreenShare | Video", example = "ScreenShare")
	private SignalMessageType type;

	@Builder
	public SdpResponseDto(UserInfoDto userInfo, SdpDto sessionDescription, SignalMessageType type) {
		this.userInfo = userInfo;
		this.sessionDescription = sessionDescription;
		this.type = type;
	}
}
