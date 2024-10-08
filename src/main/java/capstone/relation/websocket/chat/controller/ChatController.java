package capstone.relation.websocket.chat.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
	public ResponseEntity<?> newMessage(@DestinationVariable String workSpaceId, MessagePublishDto message,
		SimpMessageHeaderAccessor headerAccessor) throws Exception {
		try {
			MessageDto messageDto = chatService.sendNewMessage(workSpaceId, message.getContent(), headerAccessor);
			if (messageDto == null)
				return ResponseEntity.status(400).body("The message is empty.");
			return ResponseEntity.ok(messageDto);
		} catch (AuthException e) {
			return ResponseEntity
				.status(401)
				.body("Access token is already expired or invalid.");
			// } catch (WorkspaceException e) {
			// 	return ResponseEntity.status(404)
			// 		.body("The workspace does not exist or you do not have access to it.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@MessageMapping("/history")
	public void history(Principal principal, @Payload(required = false) HistoryPublishDto historyPublishDto,
		SimpMessageHeaderAccessor headerAccessor) throws
		Exception {
		try {
			Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
			HistoryResponseDto historyResponseDto;
			if (historyPublishDto == null) {
				historyResponseDto = chatService.getHistory(null, userId);
			} else {
				historyResponseDto = chatService.getHistory(historyPublishDto.getLastMsgId(), userId);
			}
			simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/history",
				ResponseEntity.ok(historyResponseDto));
		} catch (AuthException e) {
			simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/history",
				ResponseEntity.status(401).build());
		} catch (Exception e) {
			simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/history",
				ResponseEntity.badRequest().build());
		}
	}

	@MessageMapping("/test/connection")
	@SendTo("/topic/test/connection")
	public String testConnection() {
		System.out.println("Test connection");
		return "Test connection";
	}
}
