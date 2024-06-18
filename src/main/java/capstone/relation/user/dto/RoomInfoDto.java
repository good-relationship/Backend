package capstone.relation.user.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoDto {
	@Schema(description = "유저가 들어가 있는 방이 존재하는지 여부", example = "true")
	private boolean hasRoom = false;
	@Schema(description = "방 ID", example = "1234567890")
	private Long roomId;
	@Schema(description = "방 이름", example = "캡스톤")
	private String roomName;
	@Schema(description = "방에 속한 멤버들")
	private List<UserInfoDto> members;
}
