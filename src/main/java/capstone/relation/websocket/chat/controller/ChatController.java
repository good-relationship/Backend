package capstone.relation.websocket.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.chat.ChatService;
import capstone.relation.websocket.chat.dto.publish.HistoryPublishDto;
import capstone.relation.websocket.chat.dto.publish.MessagePublishDto;
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final SimpMessagingTemplate simpMessagingTemplate;
	MessageRepository messageRepository = new MessageRepository();

	@MessageMapping("/message/{workSpaceId}")
	@SendTo("/topic/message/{workSpaceId}")
	public ResponseEntity<MessageDto> newMessage(@DestinationVariable String workSpaceId, MessagePublishDto message,
		SimpMessageHeaderAccessor headerAccessor) throws Exception { //TODO: 토큰 만료 되는 경우 어떻게 함?
		MessageDto messageDto = chatService.sendNewMessage(workSpaceId, message.getContent(), headerAccessor);
		//TODO: NULL 처리
		System.out.println("Send message: " + messageDto);
		return ResponseEntity.ok(messageDto);
	}

	//TODO : 아직 안 했음 해야함.
	@MessageMapping("/history/{workSpaceId}")
	@SendTo("/topic/history/{worksSpaceId}")
	public ResponseEntity<HistoryResponseDto> history(@DestinationVariable String workSpaceId, HistoryPublishDto join,
		SimpMessageHeaderAccessor headerAccessor) throws
		Exception {
		HistoryResponseDto messages = new HistoryResponseDto();
		System.out.println("Received history: " + join.toString());
		List<MessageDto> mess = MessageRepository.getMessages(join.getRoomId());
		if (mess == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		messages.setMessages(mess);
		messages.setEnd(false);
		messages.setLastMsgId(mess.get(mess.size() - 1).getMessageId());
		System.out.println("반환값:" + messages);
		return ResponseEntity.ok(messages);
	}
}
