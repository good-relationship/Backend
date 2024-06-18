package capstone.relation.websocket.signaling.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import capstone.relation.user.UserService;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.websocket.signaling.dto.IceDto;
import capstone.relation.websocket.signaling.dto.SdpMessageDto;
import capstone.relation.websocket.signaling.dto.SdpResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignalingService {
	private final SocketRegistry socketRegistry;

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final UserService userService;

	public void sendOffer(String roomId, SdpMessageDto sdpMessageDto, Long myId) {
		System.out.println("sendOffer");
		String destId = sdpMessageDto.getUserId();
		String socketId = socketRegistry.getSocketId(destId);
		SdpResponseDto sdpResponseDto = new SdpResponseDto();
		sdpResponseDto.setUserInfo(userService.getUserInfo(myId));
		sdpResponseDto.setSessionDescription(sdpMessageDto.getSessionDescription());
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/offer/" + roomId, sdpResponseDto);
	}

	//TODO: 둘이 하는일 같으니까 합치자.
	public void sendAnswer(String roomId, SdpMessageDto sdpMessageDto, Long myId) {
		System.out.println("sendAnswer");
		String socketId = socketRegistry.getSocketId(sdpMessageDto.getUserId());
		SdpResponseDto sdpResponseDto = new SdpResponseDto();

		sdpResponseDto.setUserInfo(userService.getUserInfo(myId));
		sdpResponseDto.setSessionDescription(sdpMessageDto.getSessionDescription());
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/answer/" + roomId, sdpResponseDto);
	}

	public void sendIce(String roomId, IceDto iceDto, Long myId) {
		System.out.println("sendIce");
		String destId = iceDto.getUserId();
		String socketId = socketRegistry.getSocketId(destId);
		iceDto.setUserId(myId.toString()); // 보내는 사람 ID로 갈아 껴줌.
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/ice/" + roomId, iceDto);
	}

}
