package capstone.relation.websocket.chat.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import capstone.relation.websocket.chat.dto.SenderDto;
import capstone.relation.websocket.chat.dto.publish.MessagePublishDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import lombok.Data;

@Data
@Repository
public class MessageRepository {
	static Map<String, List<MessageDto>> messages = new HashMap<>();

	public static MessageDto addMessage(MessagePublishDto message) {
		MessageDto messageDto = new MessageDto();
		messageDto.setContent(message.getContent());
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = formatter.format(now);
		messageDto.setTime(formattedDate);
		SenderDto sender = new SenderDto();
		sender.setDummy();
		sender.setSenderId(message.getSenderId());
		sender.setSenderName(message.getSenderId());
		messageDto.setSender(sender);
		if (messages.get(message.getRoomId()) == null) {
			List<MessageDto> messageList = new ArrayList<>();
			messages.put(message.getRoomId(), messageList);
		}
		messages.get(message.getRoomId()).add(messageDto);
		return messageDto;
	}

	public static List<MessageDto> getMessages(String roomId) {
		return messages.get(roomId);
	}
}
