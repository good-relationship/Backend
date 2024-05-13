package capstone.relation.websocket.meeting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회의방 생성 요청")
public class CreateRoomDto {
	@Schema(description = "방 이름", example = "회의방")
	private String roomName;
}
