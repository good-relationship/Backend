package capstone.relation.websocket.meeting;

import lombok.Data;

@Data
public class WebRtcDto {
	private String sender; //보내는 유저 UUID TODO: JWT로 변경
	private String type; //SignalMessageType
	private String sdp;
	private String ice;
	private String roomId;
}
