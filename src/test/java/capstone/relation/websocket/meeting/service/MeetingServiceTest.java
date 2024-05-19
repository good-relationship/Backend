package capstone.relation.websocket.meeting.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.websocket.meeting.domain.MeetRoom;
import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.dto.response.JoinResponseDto;
import capstone.relation.websocket.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.websocket.meeting.repository.MeetRoomRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;

@ExtendWith(MockitoExtension.class)
@Transactional
class MeetingServiceTest {
	@InjectMocks
	private MeetingService meetingService;

	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;

	@Mock
	private SocketRegistry socketRegistry;

	@Mock
	private UserRepository userRepository;

	@Mock
	private WorkSpaceRepository workSpaceRepository;

	@Mock
	private MeetRoomRepository meetRoomRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private HashOperations<String, String, Set<String>> hashOperations;

	@BeforeEach
	void setUp() {
		// RedisTemplate의 opsForHash를 모킹하여 hashOperations를 반환하도록 설정합니다.
		given(redisTemplate.opsForHash()).willReturn((HashOperations)hashOperations);

		// MeetingService의 init() 메서드를 명시적으로 호출합니다.
		meetingService.init();
	}

	@DisplayName("회의 방을 생성할 수 있다.")
	@Test
	void createRoom() {
		// given
		CreateRoomDto createRoomDto = new CreateRoomDto("테스트 방이름");
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
		Map<String, Object> sessionAttributes = new HashMap<>();
		sessionAttributes.put("userId", 1L);
		sessionAttributes.put("workSpaceId", "workspace-1");
		headerAccessor.setSessionAttributes(sessionAttributes);

		given(socketRegistry.getSocketId("1")).willReturn("테스트 소켓 아이디");
		given(workSpaceRepository.findById("workspace-1")).willReturn(Optional.of(new WorkSpace()));
		given(meetRoomRepository.save(any(MeetRoom.class))).willAnswer(invocation -> {
			MeetRoom meetRoom = invocation.getArgument(0);
			meetRoom.setRoomId(1L);
			return meetRoom;
		});
		given(hashOperations.get(anyString(), anyString())).willReturn(new HashSet<>());

		// when
		meetingService.createRoom(createRoomDto, headerAccessor);

		// then
		ArgumentCaptor<JoinResponseDto> joinResponseCaptor = ArgumentCaptor.forClass(JoinResponseDto.class);
		verify(simpMessagingTemplate).convertAndSendToUser(eq("테스트 소켓 아이디"), eq("/queue/join"),
			joinResponseCaptor.capture());

		JoinResponseDto joinResponse = joinResponseCaptor.getValue();
		assertThat(joinResponse).isNotNull();
		assertThat(joinResponse.getRoomName()).isEqualTo("테스트 방이름");
		assertThat(joinResponse.getRoomId()).isEqualTo(1L);
	}

	@DisplayName("회의 방 목록을 받아올 수 있다.")
	@Test
	void getRoomList() {
		// given
		WorkSpace workSpace = new WorkSpace();
		String workSpaceId = workSpace.getId();
		MeetRoom meetRoom1 = MeetRoom.
			builder()
			.roomId(1L)
			.roomName("회의실1")
			.deleted(false)
			.workSpace(workSpace)
			.build();
		MeetRoom meetRoom2 = MeetRoom.
			builder()
			.roomId(2L)
			.roomName("회의실2")
			.deleted(false)
			.workSpace(workSpace)
			.build();

		Set<MeetRoom> meetRooms = new HashSet<>();
		meetRooms.add(meetRoom1);
		meetRooms.add(meetRoom2);
		given(meetRoomRepository.findAllByWorkSpaceId(workSpaceId)).willReturn(meetRooms);
		given(hashOperations.get(anyString(), anyString())).willReturn(new HashSet<>());
		// when
		MeetingRoomListDto roomList = meetingService.getRoomList(workSpaceId);

		// then
		assertThat(roomList).isNotNull();
		assertThat(roomList.getMeetingRoomList()).hasSize(2);
		assertThat(roomList.getMeetingRoomList().get(0).getRoomName()).isEqualTo("회의실1");
		assertThat(roomList.getMeetingRoomList().get(1).getRoomId()).isEqualTo(2L);
	}
}