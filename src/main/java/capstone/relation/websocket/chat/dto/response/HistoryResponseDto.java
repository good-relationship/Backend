package capstone.relation.websocket.chat.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class HistoryResponseDto {
	private List<MessageDto> messages;
	private boolean isEnd;
	private String lastMsgId;
}
