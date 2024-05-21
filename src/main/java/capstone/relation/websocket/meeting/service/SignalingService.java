package capstone.relation.websocket.meeting.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import capstone.relation.websocket.SocketRegistry;
import capstone.relation.websocket.meeting.dto.SdpMessageDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignalingService {
	private final SocketRegistry socketRegistry;

	private final SimpMessagingTemplate simpMessagingTemplate;

	public void sendOffer(String roomId, SdpMessageDto sdpMessageDto, Long myId) {
		System.out.println("sendOffer");
		String destId = sdpMessageDto.getUserId();
		String socketId = socketRegistry.getSocketId(destId);
		sdpMessageDto.setUserId(myId.toString()); // 보내는 사람 ID로 갈아 껴줌.
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/offer/" + roomId, sdpMessageDto);
	}
}
