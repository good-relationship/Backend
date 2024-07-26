package capstone.relation.meeting.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.dto.response.JoinResponseDto;
import capstone.relation.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.meeting.repository.MeetRoomRepository;
import capstone.relation.meeting.repository.RedisRepository;
import capstone.relation.security.WithMockCustomUser;
import capstone.relation.user.UserService;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;

@ExtendWith(SpringExtension.class)
class MeetRoomServiceTest {
	@InjectMocks
	private MeetRoomService meetRoomService;

	@Mock
	private UserService mockUserService;

	@Mock
	private SimpMessagingTemplate mockSimpMessagingTemplate;

	@Mock
	private WorkSpaceRepository mockWorkSpaceRepository;

	@Mock
	private MeetRoomRepository mockMeetRoomRepository;
	
	@Mock
	private RedisRepository mockRedisRepository;

	@DisplayName("회의 방을 생성하고 가입할 수 있다.")
	@WithMockCustomUser
	@Test
	void createAndJoinRoom() {
		// given
		CreateRoomDto createRoomDto = new CreateRoomDto("테스트 방이름");

		given(mockUserService.getUserWorkSpaceId(1L)).willReturn("workspace-1");
		given(mockWorkSpaceRepository.findById("workspace-1")).willReturn(Optional.of(new WorkSpace()));
		given(mockMeetRoomRepository.save(any(MeetRoom.class))).willAnswer(invocation -> {
			MeetRoom meetRoom = invocation.getArgument(0);
			meetRoom.setRoomId(1L);
			return meetRoom;
		});
		given(mockMeetRoomRepository.findById(1L)).willReturn(
			Optional.of(MeetRoom.builder().roomId(1L).roomName("테스트 방이름").build()));

		// when
		JoinResponseDto joinResponse = meetRoomService.createAndJoinRoom(createRoomDto);

		// then
		verify(mockSimpMessagingTemplate, times(1)).convertAndSend(eq("/topic/workspace-1/meetingRoomList"),
			any(MeetingRoomListDto.class));
		assertThat(joinResponse).isNotNull();
		assertThat(joinResponse.getRoomName()).isEqualTo("테스트 방이름");
		assertThat(joinResponse.getRoomId()).isEqualTo(1L);
	}

	@DisplayName("잘못된 회의실 이름으로 회의 방을 생성하려고 할 때 예외가 발생한다.")
	@Test
	public void createAndJoinRoomWithInvalidRoomName() {
		// given
		CreateRoomDto createRoomDto = new CreateRoomDto("");

		// when & then
		assertThatThrownBy(() -> meetRoomService.createAndJoinRoom(createRoomDto))
			.isInstanceOf(ResponseStatusException.class)
			.satisfies(exception -> {
				ResponseStatusException ex = (ResponseStatusException)exception;
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
				assertThat(ex.getReason()).isEqualTo("회의실 이름을 입력해주세요.");
			});
	}

	@DisplayName("이미 회의실에 참여한 사용자가 다시 참여하려고 할 때 예외가 발생한다.")
	@WithMockCustomUser
	@Test
	public void createAndJoinRoomWithAlreadyJoinedUser() {
		// given
		CreateRoomDto createRoomDto = new CreateRoomDto("테스트 방이름");

		given(mockUserService.getUserWorkSpaceId(1L)).willReturn("workspace-1");
		given(mockWorkSpaceRepository.findById("workspace-1")).willReturn(Optional.of(new WorkSpace()));
		given(mockRedisRepository.isUserInRoom(1L)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> meetRoomService.createAndJoinRoom(createRoomDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("User is already in the room: 1");
	}

	@DisplayName("워크스페이스에 참여한 모든 유저에게 회의실 목록을 전송할 수 있다.")
	@Test
	public void sendRoomList() {
		// given
		given(mockWorkSpaceRepository.findById("workspace-1")).willReturn(Optional.of(new WorkSpace()));

		// when
		meetRoomService.sendRoomList("workspace-1");

		// then
		verify(mockSimpMessagingTemplate, times(1)).convertAndSend(eq("/topic/workspace-1/meetingRoomList"),
			any(MeetingRoomListDto.class));
	}

	@DisplayName("회의실을 나갈 수 있다.")
	@WithMockCustomUser
	@Test
	public void leaveRoom() {
		// given
		given(mockUserService.getUserWorkSpaceId(1L)).willReturn("workspace-1");
		given(mockRedisRepository.isUserInRoom(1L)).willReturn(true);
		given(mockRedisRepository.getUserRoomId(1L)).willReturn("1");

		// when
		meetRoomService.leaveRoom(1L);

		// then
		verify(mockRedisRepository, times(1)).removeUserFromRoom("workspace-1", 1L, "1");
	}
}
