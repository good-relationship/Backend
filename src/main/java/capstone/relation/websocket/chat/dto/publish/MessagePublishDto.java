package capstone.relation.websocket.chat.dto.publish;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MessagePublishDto {
	@Schema(description = "메시지 내용", example = "안녕하세요!")
	private String content;
}
