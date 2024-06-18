package capstone.relation.websocket.chat.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoryResponseDto {
	@Schema(description = "메시지 목록")
	private List<MessageDto> messages;
	@Schema(description = "더 이상 메시지가 없는지 여부", example = "false")
	private boolean isEnd;
	@Schema(description = "시작 메시지인지 여부", example = "false")
	private boolean isStart;
	@Schema(description = "마지막 메시지 ID", example = "1234567890")
	private Long lastMsgId;
}
