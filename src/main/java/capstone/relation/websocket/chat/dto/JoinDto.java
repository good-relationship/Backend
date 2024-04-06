package capstone.relation.websocket.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinDto {
	private String roomId; // 방 번호
	private String sender; // 채팅을 보낸 사람
}
