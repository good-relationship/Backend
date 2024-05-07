package capstone.relation.websocket.meeting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetingController {
	@MessageMapping("/createRoom")
	public ResponseEntity<?> signal() {
		return ResponseEntity.ok().build();
	}

	@MessageMapping("/joinRoom")
	public ResponseEntity<?> joinRoom() {
		return ResponseEntity.ok().build();
	}

	@MessageMapping("/leaveRoom")
	public ResponseEntity<?> leaveRoom() {
		return ResponseEntity.ok().build();
	}

}
