package good_relation.good_relation.websocket.chat.repository;

import java.util.ArrayList;
import java.util.List;

import good_relation.good_relation.websocket.chat.dto.ChatDto;
import lombok.Data;

@Data
public class MessageRepository {
	static List<ChatDto> messages = new ArrayList<>();

	public static void addMessage(ChatDto message) {
		messages.add(message);
	}

	public static List<ChatDto> getMessages(String roomId) {
		return messages;
	}
}
