package capstone.relation.websocket.chat.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.chat.dto.ChatDto;
import capstone.relation.websocket.chat.dto.JoinDto;
import capstone.relation.websocket.chat.repository.MessageRepository;

@Controller
public class ChatController {

	MessageRepository messageRepository = new MessageRepository();

	@MessageMapping("/message")
	@SendTo("/topic/message")
	public ChatDto newMessage(ChatDto message) throws Exception {
		System.out.println("Received message: " + message.getContent());
		messageRepository.addMessage(message);
		return message;
	}

	@MessageMapping("/history")
	@SendTo("/topic/history")
	public List<ChatDto> history(JoinDto join) throws Exception {
		List<ChatDto> messages = messageRepository.getMessages(join.getRoomId());
		return messages;
	}

}
