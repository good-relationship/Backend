package capstone.relation.websocket.meeting.signaling;

public enum SignalMessageType {
	
	OFFER, ANSWER, ICE, JOIN, LEAVE;

	// enum에서 제공하는 toString()을 사용하면 name()과 동일한 값을 반환합니다.
	@Override
	public String toString() {
		return name().toLowerCase();  // "offer", "answer" 등 소문자로 반환
	}
}
