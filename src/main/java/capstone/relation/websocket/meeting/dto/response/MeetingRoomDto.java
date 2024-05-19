package capstone.relation.websocket.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MeetingRoomDto {
	@Schema(description = "회의실 이름", example = "회의실1")
	String roomName;
	@Schema(description = "회의실 ID", example = "123")
	Long roomId;
	@Schema(description = "회의실 사용자 수", example = "2")
	int userCount;
}
