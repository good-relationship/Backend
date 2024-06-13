package capstone.relation.meeting.service;

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

import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.dto.response.JoinResponseDto;
import capstone.relation.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.meeting.repository.MeetRoomRepository;
import capstone.relation.user.domain.Role;
import capstone.relation.user.domain.User;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;

@ExtendWith(MockitoExtension.class)
class MeetRoomServiceTest {
	@InjectMocks
	private MeetRoomService meetRoomService;

	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;

	@Mock
	private SocketRegistry socketRegistry;

	@Mock
	private WorkSpaceRepository workSpaceRepository;

	@Mock
	private MeetRoomRepository meetRoomRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private HashOperations<String, String, HashMap<String, Set<String>>> workspaceRoomParticipants;
	//workspaceId, roomId, userIds
	@Mock
	private HashOperations<String, String, String> mockUserRoomMapping;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		// RedisTemplate의 opsForHash를 모킹하여 hashOperations를 반환하도록 설정합니다.
		when(redisTemplate.opsForHash()).thenReturn((HashOperations)workspaceRoomParticipants)
			.thenReturn(mockUserRoomMapping);
		// MeetingService의 init() 메서드를 명시적으로 호출합니다.
		meetRoomService.init();
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

		given(workSpaceRepository.findById("workspace-1")).willReturn(Optional.of(new WorkSpace()));
		given(meetRoomRepository.save(any(MeetRoom.class))).willAnswer(invocation -> {
			MeetRoom meetRoom = invocation.getArgument(0);
			meetRoom.setRoomId(1L);
			return meetRoom;
		});
		given(workspaceRoomParticipants.get(anyString(), anyString())).willReturn(new HashMap<>());
		given(meetRoomRepository.findById(1L)).willReturn(
			Optional.of(MeetRoom.builder().roomId(1L).roomName("테스트 방이름").build()));

		// when
		JoinResponseDto joinResponse = meetRoomService.createAndJoinRoom(createRoomDto, 1L, "workspace-1");

		// then
		verify(simpMessagingTemplate, times(1)).convertAndSend(eq("/topic/workspace-1/meetingRoomList"),
			any(MeetingRoomListDto.class));
		assertThat(joinResponse).isNotNull();
		assertThat(joinResponse.getRoomName()).isEqualTo("테스트 방이름");
		assertThat(joinResponse.getRoomId()).isEqualTo(1L);
	}

	@DisplayName("회의 방 목록을 받아올 수 있다.")
	@Test
	void getRoomList() {
		// given
		WorkSpace workSpace = new WorkSpace();
		workSpace.setId("workspace-1");
		String workSpaceId = workSpace.getId();
		MeetRoom meetRoom1 = MeetRoom.builder().roomId(1L).roomName("회의실1").deleted(false).workSpace(workSpace).build();
		MeetRoom meetRoom2 = MeetRoom.builder().roomId(2L).roomName("회의실2").deleted(false).workSpace(workSpace).build();

		Set<MeetRoom> meetRooms = new HashSet<>();
		meetRooms.add(meetRoom1);
		meetRooms.add(meetRoom2);
		Set<String> userIds = new HashSet<>();
		userIds.add("1");
		userIds.add("2");
		HashMap<String, Set<String>> mockParti = new HashMap<>();
		mockParti.put("1", userIds);
		mockParti.put("2", userIds);
		given(meetRoomRepository.findAllByWorkSpaceId(workSpaceId)).willReturn(meetRooms);
		given(workspaceRoomParticipants.get(anyString(), anyString())).willReturn(mockParti);

		// when
		meetRoomService.sendRoomList(workSpaceId);

		// then
		ArgumentCaptor<MeetingRoomListDto> roomListCaptor = ArgumentCaptor.forClass(MeetingRoomListDto.class);
		verify(simpMessagingTemplate, times(1)).convertAndSend(eq("/topic/workspace-1/meetingRoomList"),
			roomListCaptor.capture());

		MeetingRoomListDto roomList = roomListCaptor.getValue();
		assertThat(roomList).isNotNull();
		assertThat(roomList.getMeetingRoomList()).hasSize(2);
		assertThat(roomList.getMeetingRoomList()).anyMatch(
			room -> room.getRoomId().equals(1L) && room.getRoomName().equals("회의실1"));
		assertThat(roomList.getMeetingRoomList()).anyMatch(
			room -> room.getRoomId().equals(2L) && room.getRoomName().equals("회의실2"));
	}

	@DisplayName("회의 방에 참여할 수 있다.")
	@Test
	void joinRoom() {
		// given
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
		Map<String, Object> sessionAttributes = new HashMap<>();
		sessionAttributes.put("userId", 1L);
		sessionAttributes.put("workSpaceId", "workspace-1");
		headerAccessor.setSessionAttributes(sessionAttributes);

		WorkSpace workSpace = new WorkSpace();
		workSpace.setId("workspace-1");
		MeetRoom meetRoom1 = MeetRoom.builder().roomId(1L).roomName("회의실1").deleted(false).workSpace(workSpace).build();
		given(meetRoomRepository.findById(1L)).willReturn(Optional.of(meetRoom1));
		given(userRepository.findById(1L)).willReturn(Optional.of(
			User.builder()
				.id(1L)
				.email("wnddms12345@naver.com")
				.profileImage("https://avatars.githubusercontent.com/u/77449538?v=4")
				.userName("김민수")
				.provider("github")
				.role(Role.USER)
				.build()));
		given(workspaceRoomParticipants.get(anyString(), anyString())).willReturn(new HashMap<>());
		// when
		JoinResponseDto joinResponseDto = meetRoomService.joinRoom(1L, "workspace-1", 1L);

		// then
		assertThat(joinResponseDto).isNotNull();
		assertThat(joinResponseDto.getRoomId()).isEqualTo(1L);
		assertThat(joinResponseDto.getRoomName()).isEqualTo("회의실1");
	}
}
