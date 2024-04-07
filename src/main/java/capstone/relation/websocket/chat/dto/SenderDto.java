package capstone.relation.websocket.chat.dto;

import lombok.Data;

@Data
public class SenderDto {
	private String senderName;
	private String senderImage;
	private String senderId;

	public void setDummy() {
		this.senderName = "dummy";
		this.senderImage = "https://lh3.googleusercontent.com/a/ACg8ocIkB4y_nIpfA3LnRUU6KifqRBbiqy_eSyFleZtLf7DEzzg4Cftz=s96-c"; //내 판다 이미지
		this.senderId = "dummy";
	}
}
