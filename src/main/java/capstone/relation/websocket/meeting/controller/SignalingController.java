package capstone.relation.websocket.meeting.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import capstone.relation.websocket.meeting.dto.SignalMessage;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SignalingController {

	@MessageMapping("/message")
	@SendTo("/topic/messages")
	public SignalMessage send(SignalMessage message) throws Exception {
		System.out.println("!!!!!MESSAGE!!!!");
		System.out.println(message.getMessage());
		System.out.println(message);
		System.out.println("!!!!!MESSAGE!!!!");
		return message;
	}

	@MessageMapping("/ice{roomId}")
	public void ice() {
		System.out.println("ice");
	}

	@MessageMapping("/offer/{roomId}")
	public void offer() {
	}

	@MessageMapping("/answer")
	public void answer() {

	}

}
