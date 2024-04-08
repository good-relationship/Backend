package capstone.relation.websocket.chat.dto.publish;

import lombok.Data;

@Data
public class HistoryPublishDto {
	private String lastMsgId;
	private String senderId;
	private String roomId;
}
