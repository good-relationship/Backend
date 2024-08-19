package capstone.relation.websocket.signaling;

import static capstone.relation.user.domain.Role.*;
import static capstone.relation.websocket.signaling.dto.SignalMessageType.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import capstone.relation.api.auth.jwt.TokenProvider;
import capstone.relation.user.domain.User;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.signaling.dto.IceDto;
import capstone.relation.websocket.signaling.dto.SdpDto;
import capstone.relation.websocket.signaling.dto.SdpMessageDto;
import capstone.relation.websocket.signaling.dto.SdpResponseDto;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") //테스트 프로필 활성화.
@DisplayName("시그널링 통합 테스트")
@ExtendWith(SpringExtension.class) //단위 테스트에 공통적으로 사용할 확장 기능을 선언
public class SignalingIntegrationTest {
	@LocalServerPort
	private int port;

	private String WEBSOCKET_URI;
	private String WEBSOCKET_TOPIC;

	private WebSocketStompClient stompClient;
	private User sender;
	private User receiver;

	private Date accessTokenExpiredDate;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private WorkSpaceRepository workSpaceRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		this.WEBSOCKET_URI = "ws://localhost:" + port + "/ws-chat";
		this.WEBSOCKET_TOPIC = "/app";
		this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		sender = User.builder()
			.email("wnddms12345@gmail.com")
			.profileImage("profileImage")
			.provider("local")
			.userName("senderName")
			.role(USER)
			.build();
		WorkSpace workSpace = new WorkSpace();
		workSpace.setName("workSpaceName");
		workSpaceRepository.save(workSpace);
		sender.setWorkSpace(workSpace);
		userRepository.save(sender);

		receiver = User.builder()
			.email("wnddms12345@gmail.com")
			.profileImage("profileImage")
			.provider("local")
			.userName("receiverName")
			.role(USER)
			.build();
		receiver.setWorkSpace(workSpace);
		userRepository.save(receiver);

		accessTokenExpiredDate = new Date(new Date().getTime() + 10000000L);
	}

	private StompSession connectWebSocket(User user) throws
		InterruptedException,
		ExecutionException {

		System.out.println("유저 ID: " + user.getId());
		String token = tokenProvider.generateAccessToken(user, accessTokenExpiredDate);
		System.out.println("임시 엑세스 토큰 생성: " + token);

		//연결 시 사용하는 JWT 설정
		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		//STOMP 사용시 사용하는 JWT 설정
		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.add("Authorization", "Bearer " + token);

		//STOMP 연결
		StompSession session = stompClient.connectAsync(WEBSOCKET_URI, headers, stompHeaders,
			new StompSessionHandlerAdapter() {
				@Override
				public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
					System.out.println("STOMP 연결 성공");
				}
			}).get();

		return session;
	}

	@Test
	@DisplayName("소켓으로 연결된 유저에게 ice 메시지 전송할 수 있다.")
	public void testIce() throws InterruptedException, ExecutionException, TimeoutException {
		BlockingQueue<IceDto> messageQueue = new LinkedBlockingQueue<>();

		IceDto iceDto = new IceDto();
		iceDto.setType(ScreenShare);
		iceDto.setCandidate("candidate");
		iceDto.setSdpMid("sdpMid");
		iceDto.setSdpMLineIndex("sdpMLineIndex");
		iceDto.setUserId(receiver.getId().toString());
		//유저 2명 연결 1이 송신 2가 수신.
		StompSession senderSession = connectWebSocket(sender);
		StompSession receiverSession = connectWebSocket(receiver);
		receiverSession.subscribe("/user/queue/ice/123", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				System.out.println("구독 시작: /user/queue/ice/123");
				return IceDto.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				System.out.println("ice 응답값 : " + payload);
				messageQueue.offer((IceDto)payload);
			}
		});
		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.setDestination(WEBSOCKET_TOPIC + "/ice/123");

		senderSession.send(stompHeaders, iceDto);

		IceDto receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS);
		assertThat(receivedMessage).isNotNull();
		assertThat(receivedMessage.getUserId()).isEqualTo(sender.getId().toString());
	}

	@Test
	@DisplayName("offer 테스트")
	public void testOffer() throws InterruptedException, ExecutionException, TimeoutException {
		BlockingQueue<SdpResponseDto> messageQueue = new LinkedBlockingQueue<>();

		SdpMessageDto sdpMessageDto = new SdpMessageDto();
		sdpMessageDto.setUserId(receiver.getId().toString());
		sdpMessageDto.setType(ScreenShare);
		SdpDto sdpDto = new SdpDto();
		sdpDto.setSdp("sdp");
		sdpDto.setType("offer");
		sdpMessageDto.setSessionDescription(sdpDto);

		//유저 2명 연결 1이 송신 2가 수신.
		StompSession senderSession = connectWebSocket(sender);
		StompSession receiverSession = connectWebSocket(receiver);
		receiverSession.subscribe("/user/queue/offer/123", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				System.out.println("구독 시작: /user/queue/offer/123");
				return SdpResponseDto.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				System.out.println("offer 응답값 : " + payload);
				messageQueue.offer((SdpResponseDto)payload);
			}
		});

		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.setDestination(WEBSOCKET_TOPIC + "/offer/123");

		senderSession.send(stompHeaders, sdpMessageDto);

		SdpResponseDto receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS);
		assertThat(receivedMessage).isNotNull();
		assertThat(receivedMessage.getUserInfo().getUserId()).isEqualTo(sender.getId());
	}

	@Test
	@DisplayName("소켓으로 연결된 유저에게 answer 메시지 전송할 수 있다.")
	public void answerTest() throws InterruptedException, ExecutionException, TimeoutException {
		BlockingQueue<SdpResponseDto> messageQueue = new LinkedBlockingQueue<>();

		SdpMessageDto sdpMessageDto = new SdpMessageDto();
		SdpDto sdpDto = new SdpDto();
		sdpDto.setSdp("sdp");
		sdpDto.setType("answer");
		sdpMessageDto.setSessionDescription(sdpDto);
		sdpMessageDto.setUserId(receiver.getId().toString());
		sdpMessageDto.setType(ScreenShare);

		//유저 2명 연결 1이 송신 2가 수신.
		StompSession senderSession = connectWebSocket(sender);
		StompSession receiverSession = connectWebSocket(receiver);
		receiverSession.subscribe("/user/queue/answer/123", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				System.out.println("구독 시작: /user/queue/answer/123");
				return SdpResponseDto.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				System.out.println("ice 응답값 : " + payload);
				messageQueue.offer((SdpResponseDto)payload);
			}
		});
		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.setDestination(WEBSOCKET_TOPIC + "/answer/123");

		senderSession.send(stompHeaders, sdpMessageDto);

		SdpResponseDto receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS);
		assertThat(receivedMessage).isNotNull();
		// assertThat(receivedMessage.getUserId()).isEqualTo(sender.getId().toString());
	}
}
