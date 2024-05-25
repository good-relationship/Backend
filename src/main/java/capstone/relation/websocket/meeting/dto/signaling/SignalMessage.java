package capstone.relation.websocket.meeting.dto.signaling;

import lombok.Data;

@Data
public class SignalMessage {
	private String type;
	private String room;
	private String sender;
	private String message;

	// getters and setters
}
