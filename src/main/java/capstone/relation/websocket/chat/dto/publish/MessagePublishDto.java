package capstone.relation.websocket.chat.dto.publish;

import lombok.Data;

@Data
public class MessagePublishDto {
	private String roomId;
	private String senderId;
	private String content;
}
