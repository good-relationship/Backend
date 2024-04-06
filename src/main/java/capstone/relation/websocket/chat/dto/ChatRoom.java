package capstone.relation.websocket.chat.dto;

import java.util.HashMap;
import java.util.UUID;

import lombok.Data;

@Data
public class ChatRoom {
	private String roomId; // 채팅방 아이디
	private String roomName; // 채팅방 이름
	private long userCount; // 채팅방 인원수

	private HashMap<String, String> userlist = new HashMap<String, String>();

	public ChatRoom create(String roomName) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.roomId = UUID.randomUUID().toString();
		chatRoom.roomName = roomName;

		return chatRoom;
	}
}
