package capstone.relation.websocket.chat;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import capstone.relation.user.domain.User;
import capstone.relation.user.exception.UserException;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.ChatRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.exception.WorkSpaceException;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService 단위 테스트")
class ChatServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ChatRepository chatRepository;

	@InjectMocks
	private ChatService chatService;

	private User validUser;
	private WorkSpace validWorkSpace;

	@BeforeEach
	void setUp() {
		validWorkSpace = new WorkSpace();
		validUser = User.builder()
			.email("example@example.com")
			.build();
		validUser.setWorkSpace(validWorkSpace);
	}

	private void mockUserRepository(Long userId) {
		when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));
	}

	@Test
	@DisplayName("사용자 ID가 유효하고 첫번째 메시지인 경우 10개의 메시지를 반환한다.")
	void whenUserIdIsValidAndWorkSpaceExists_thenReturnHistory() {
		// given
		Long userId = 1L;
		Long lastMsgId = null;

		List<Chat> chats = Arrays.asList(new Chat(), new Chat());
		List<MessageDto> messageDtos = Arrays.asList(new MessageDto(), new MessageDto());

		mockUserRepository(userId);
		when(chatRepository.findTop11ByWorkSpaceOrderByIdDesc(validWorkSpace)).thenReturn(chats);

		// when
		HistoryResponseDto response = chatService.getHistory(lastMsgId, userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.isStart()).isTrue();
		assertThat(response.isEnd()).isTrue();
		assertThat(response.getMessages()).isEqualTo(messageDtos);

		verify(userRepository, times(1)).findById(userId);
		verify(chatRepository, times(1)).findTop11ByWorkSpaceOrderByIdDesc(validWorkSpace);
	}

	@Test
	@DisplayName("사용자 ID가 유효하지 않으면 User Exception 을 던진다.")
	void whenUserIdIsInvalid_thenThrowAuthException() {
		// given
		Long userId = 1L;
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(UserException.class, () -> chatService.getHistory(null, userId));

		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("작업 공간이 없으면 WorkSpace Exception 을 던진다.")
	void whenUserHasNoWorkSpace_thenThrowAuthException() {
		// given
		Long userId = 1L;
		validUser.setWorkSpace(null);
		mockUserRepository(userId);

		// when & then
		assertThrows(WorkSpaceException.class, () -> chatService.getHistory(null, userId));

		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("마지막 메시지 ID가 주어지면 해당 ID 이후의 메시지 목록을 반환한다.")
	void whenLastMsgIdIsProvided_thenReturnHistoryAfterThatMessage() {
		// given
		Long userId = 1L;
		Long lastMsgId = 100L;

		List<Chat> chats = Arrays.asList(new Chat(), new Chat(), new Chat());
		List<MessageDto> messageDtos = Arrays.asList(new MessageDto(), new MessageDto(), new MessageDto());

		mockUserRepository(userId);
		when(chatRepository.findTop11ByWorkSpaceAndIdIsLessThanOrderByIdDesc(validWorkSpace, lastMsgId))
			.thenReturn(chats);

		// when
		HistoryResponseDto response = chatService.getHistory(lastMsgId, userId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.isStart()).isFalse();
		assertThat(response.isEnd()).isTrue();
		assertThat(response.getMessages()).isEqualTo(messageDtos);

		verify(userRepository, times(1)).findById(userId);
		verify(chatRepository, times(1))
			.findTop11ByWorkSpaceAndIdIsLessThanOrderByIdDesc(validWorkSpace, lastMsgId);
	}

}
