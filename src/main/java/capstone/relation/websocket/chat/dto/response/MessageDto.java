package capstone.relation.websocket.chat.dto.response;

import capstone.relation.websocket.chat.dto.SenderDto;
import lombok.Data;

@Data
public class MessageDto {
	private SenderDto sender;
	private String time; // LocalDateTime yyyy-MM-dd HH:mm:ss
	private String messageId;
	private String content;
}
