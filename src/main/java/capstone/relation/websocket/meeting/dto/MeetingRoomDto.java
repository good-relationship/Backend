package capstone.relation.websocket.meeting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MeetingRoomDto {
	@Schema(description = "회의실 이름", example = "회의실1")
	String name;
	@Schema(description = "회의실 ID", example = "123")
	String roomId;
	@Schema(description = "회의실 사용자 수", example = "2")
	String userCount;
}
