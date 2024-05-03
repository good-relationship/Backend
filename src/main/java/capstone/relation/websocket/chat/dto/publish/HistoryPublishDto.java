package capstone.relation.websocket.chat.dto.publish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HistoryPublishDto {
	@Schema(description = "마지막 메시지 ID", example = "1234567890")
	private Long lastMsgId;
}
