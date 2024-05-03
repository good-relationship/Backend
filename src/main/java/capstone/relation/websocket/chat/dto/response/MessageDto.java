package capstone.relation.websocket.chat.dto.response;

import capstone.relation.websocket.chat.dto.SenderDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MessageDto {
	@Schema(description = "메시지 ID", example = "1234567890")
	private SenderDto sender;
	@Schema(description = "메시지 시간", example = "2021-08-01 12:34:56")
	private String time; // LocalDateTime yyyy-MM-dd HH:mm:ss
	@Schema(description = "메시지 ID", example = "1234567890")
	private String messageId;
	@Schema(description = "메시지 내용", example = "안녕하세요!")
	private String content;
}
