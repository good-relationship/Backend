package capstone.relation.websocket.chat.controller;

import java.security.Principal;

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
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
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
		SimpMessageHeaderAccessor headerAccessor) throws Exception {
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
		try {
			HistoryResponseDto historyResponseDto = chatService.getHistory(historyPublishDto.getLastMsgId(),
				headerAccessor);
			System.out.println("Send history: " + historyResponseDto);
			simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/history",
				ResponseEntity.ok(historyResponseDto));
		} catch (AuthException e) {
			simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/history",
				ResponseEntity.status(401).build());
		} catch (Exception e) {
			simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/history",
				ResponseEntity.badRequest().build());
		}
	}
}
