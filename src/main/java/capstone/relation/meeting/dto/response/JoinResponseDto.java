package capstone.relation.meeting.dto.response;

import java.util.List;

import capstone.relation.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JoinResponseDto {
	@Schema(description = "회의실 ID", example = "123")
	private Long roomId;
	@Schema(description = "회의실 이름", example = "회의실1")
	private String roomName;
	@Schema(description = "회의실 참여자 목록, 들어올 때 당시에 목록 전체를 보내줍니다.")
	private List<UserInfoDto> userInfoList;
	@Schema(description = "회의실 참여자 수 처음 create하면 본인만 포함돼서 1로 들어갑니다.", example = "1")
	private Long userCount;
}
