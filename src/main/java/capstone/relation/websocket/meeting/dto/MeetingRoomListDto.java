package capstone.relation.websocket.meeting.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MeetingRoomListDto {
	@Schema(description = "회의실 목록")
	List<MeetingRoomDto> meetingRoomList;
}
