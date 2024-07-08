package capstone.relation.websocket.signaling.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 타입 : 화면 공유 또는 비디오")
public enum SignalMessageType {

	ScreenShare, Video;

	// enum에서 제공하는 toString()을 사용하면 name()과 동일한 값을 반환합니다.
	@Override
	public String toString() {
		return name().toLowerCase();  // "offer", "answer" 등 소문자로 반환
	}
}
