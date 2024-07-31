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
	private final UserService userService;
	private final SocketRegistry socketRegistry;
	private final SimpMessagingTemplate simpMessagingTemplate;

	/**
	 * Offer 메시지를 상대방에게 전송합니다.(목적지는 메시지를 확인해서 소켓 ID를 찾아서 전송합니다.)
	 * /user/queue/offer/{roomId} 로 전송합니다.
	 * @param roomId 참여중인 화상회의방 ID
	 * @param sdpMessageDto Offer 메시지 DTO
	 * @param senderId 보내는 사람 ID (내 Id)
	 */
	public void sendOffer(String roomId, SdpMessageDto sdpMessageDto, Long senderId) {
		String destId = sdpMessageDto.getUserId();
		String socketId = socketRegistry.getSocketId(destId);

		SdpResponseDto sdpResponseDto = SdpResponseDto.builder()
			.userInfo(userService.getUserInfo(senderId))
			.sessionDescription(sdpMessageDto.getSessionDescription())
			.type(sdpMessageDto.getType())
			.build();
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/offer/" + roomId, sdpResponseDto);
	}

	/**
	 * Answer 메시지를 상대방에게 전송합니다.(목적지는 메시지를 확인해서 소켓 ID를 찾아서 전송합니다.)
	 * /user/queue/answer/{roomId} 로 전송합니다.
	 * @param roomId 참여중인 화상회의방 ID
	 * @param sdpMessageDto Answer 메시지 DTO
	 * @param senderId 보내는 사람 ID (내 Id)
	 */
	public void sendAnswer(String roomId, SdpMessageDto sdpMessageDto, Long senderId) {
		String socketId = socketRegistry.getSocketId(sdpMessageDto.getUserId());

		SdpResponseDto sdpResponseDto = SdpResponseDto.builder()
			.userInfo(userService.getUserInfo(senderId))
			.sessionDescription(sdpMessageDto.getSessionDescription())
			.type(sdpMessageDto.getType())
			.build();
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/answer/" + roomId, sdpResponseDto);
	}

	/**
	 * Ice 메시지를 상대방에게 전송합니다.(목적지는 메시지를 확인해서 소켓 ID를 찾아서 전송합니다.)
	 * @param roomId 참여중인 화상회의방 ID
	 * @param iceDto Ice 메시지 DTO
	 * @param senderId 보내는 사람 ID (내 Id)
	 */
	public void sendIce(String roomId, IceDto iceDto, Long senderId) {
		String destId = iceDto.getUserId();
		String socketId = socketRegistry.getSocketId(destId);
		iceDto.setUserId(senderId.toString()); // 보내는 사람 ID로 갈아 껴줌.
		iceDto.setType(iceDto.getType());
		simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/ice/" + roomId, iceDto);
	}
}
