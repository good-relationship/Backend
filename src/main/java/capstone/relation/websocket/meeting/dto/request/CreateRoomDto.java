package capstone.relation.websocket.meeting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회의방 생성 요청")
@Getter
@Setter
public class CreateRoomDto {
	@Schema(description = "방 이름", example = "회의방")
	private String roomName;

	CreateRoomDto() {
	}

	public CreateRoomDto(String roomName) {
		this.roomName = roomName;
	}
}
