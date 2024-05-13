package capstone.relation.websocket.meeting.dto.response;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MeetingRoomListDto {
	@Schema(description = "회의실 목록")
	Set<MeetingRoomDto> meetingRoomList = new HashSet<>();
}
