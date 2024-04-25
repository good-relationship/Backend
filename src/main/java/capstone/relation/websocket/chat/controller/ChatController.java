package capstone.relation.websocket.chat.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.chat.dto.publish.HistoryPublishDto;
import capstone.relation.websocket.chat.dto.publish.MessagePublishDto;
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.MessageRepository;

@Controller
public class ChatController {

	MessageRepository messageRepository = new MessageRepository();

	@MessageMapping("/message")
	@SendTo("/topic/message")
	public MessageDto newMessage(MessagePublishDto message) throws Exception {
		System.out.println("Received message: " + message.getContent());
		System.out.println("Received message: " + message.getRoomId());
		MessageDto messageDto = MessageRepository.addMessage(message);
		System.out.println("반환값:" + messageDto);
		return messageDto;
	}

	@MessageMapping("/history")
	@SendTo("/topic/history")
	public HistoryResponseDto history(HistoryPublishDto join) throws Exception {
		HistoryResponseDto messages = new HistoryResponseDto();
		System.out.println("Received history: " + join.toString());
		List<MessageDto> mess = MessageRepository.getMessages(join.getRoomId());
		if (mess == null) {
			return messages;
		}
		messages.setMessages(mess);
		messages.setEnd(false);
		messages.setLastMsgId(mess.get(mess.size() - 1).getMessageId());
		System.out.println("반환값:" + messages);
		return messages;
	}
}
