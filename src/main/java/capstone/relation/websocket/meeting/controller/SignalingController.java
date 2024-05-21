package capstone.relation.websocket.meeting.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.meeting.dto.SdpMessageDto;
import capstone.relation.websocket.meeting.service.SignalingService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SignalingController {

	private final SignalingService signalingService;

	@MessageMapping("/ice{roomId}")
	public void ice() {
		System.out.println("ice");
	}

	@MessageMapping("/offer/{roomId}")
	public void offer(@DestinationVariable String roomId, SdpMessageDto sdpMessageDto,
		SimpMessageHeaderAccessor headerAccessor) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
		signalingService.sendOffer(roomId, sdpMessageDto, userId);
	}

	@MessageMapping("/answer/{roomId}")
	public void answer(@DestinationVariable String roomId, SdpMessageDto sdpMessageDto,
		SimpMessageHeaderAccessor headerAccessor) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
		signalingService.sendAnswer(roomId, sdpMessageDto, userId);
	}

}
