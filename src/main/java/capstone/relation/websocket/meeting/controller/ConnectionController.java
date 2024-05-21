package capstone.relation.websocket.meeting.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConnectionController {
	@MessageMapping("/ice{roomId}")
	public void ice() {
	}

	@MessageMapping("/offer/{roomId}")
	public void offer() {
	}

	@MessageMapping("/answer")
	public void answer() {

	}

}
