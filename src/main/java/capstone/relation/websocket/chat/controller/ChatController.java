package capstone.relation.websocket.chat.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.websocket.chat.ChatService;
import capstone.relation.websocket.chat.dto.publish.HistoryPublishDto;
import capstone.relation.websocket.chat.dto.publish.MessagePublishDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/message/{workSpaceId}")
	@SendTo("/topic/message/{workSpaceId}")
	public ResponseEntity<MessageDto> newMessage(@DestinationVariable String workSpaceId, MessagePublishDto message,
		SimpMessageHeaderAccessor headerAccessor) throws Exception { //TODO: 토큰 만료 되는 경우 어떻게 함?
		try {
			MessageDto messageDto = chatService.sendNewMessage(workSpaceId, message.getContent(), headerAccessor);
			System.out.println("Send message: " + messageDto);
			if (messageDto == null) {
				return ResponseEntity.badRequest().build();
			}
			return ResponseEntity.ok(messageDto);
		} catch (AuthException e) {
			return ResponseEntity.status(401).build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@MessageMapping("/history")
	public void history(Principal principal, HistoryPublishDto historyPublishDto,
		SimpMessageHeaderAccessor headerAccessor) throws
		Exception {
		List<MessageDto> messages = chatService.getHistory(historyPublishDto.getLastMsgId(), headerAccessor);
		System.out.println("Send history: " + messages);
		System.out.println("Principal: " + principal.getName());
		simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/history", ResponseEntity.ok(messages));
	}
}
