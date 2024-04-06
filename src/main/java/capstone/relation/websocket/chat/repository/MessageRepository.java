package capstone.relation.websocket.chat.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import capstone.relation.websocket.chat.dto.ChatDto;
import lombok.Data;

@Data
@Repository
public class MessageRepository {
	static List<ChatDto> messages = new ArrayList<>();

	public static void addMessage(ChatDto message) {
		messages.add(message);
	}

	public static List<ChatDto> getMessages(String roomId) {
		return messages;
	}
}
