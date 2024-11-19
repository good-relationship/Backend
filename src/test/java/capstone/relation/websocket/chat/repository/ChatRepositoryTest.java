package capstone.relation.websocket.chat.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DisplayName("ChatRepository JPA 테스트")
class ChatRepositoryTest {
	@Autowired
	private ChatRepository chatRepository;

	private WorkSpace workSpace;
	@Autowired
	WorkSpaceRepository workSpaceRepository;

	@BeforeEach
	void setUp() {
		workSpace = new WorkSpace();
		workSpace.setName("Test WorkSpace");
		workSpaceRepository.save(workSpace);
		for (int i = 1; i <= 15; i++) {
			Chat chat = Chat.builder()
				.content("Message " + i)
				.workSpace(workSpace)
				.timestamp(LocalDateTime.now())
				.build();
			chatRepository.save(chat);
		}
	}

	@Test
	@DisplayName("특정 WorkSpace와 lastMsgId 이전의 메시지 11개를 가져온다.")
	void findTop11ByWorkSpaceAndIdIsLessThanOrderByIdDesc_ShouldReturn11ChatsBeforeLastMsgId() {
		// given
		Long lastMsgId = 13L;

		// when
		List<Chat> result = chatRepository.findTop11ByWorkSpaceAndIdIsLessThanOrderByIdDesc(workSpace, lastMsgId);

		// then
		assertThat(result.get(0).getId()).isEqualTo(12L);
		assertThat(result).hasSize(11);
		assertThat(result.get(0).getId()).isLessThan(lastMsgId);
	}

	@Test
	@DisplayName("특정 WorkSpace의 최신 메시지 11개를 가져온다.")
	void findTop11ByWorkSpaceOrderByIdDesc_ShouldReturnLatest11Chats() {
		// when
		List<Chat> result = chatRepository.findTop11ByWorkSpaceOrderByIdDesc(workSpace);

		// then
		assertThat(result.get(0).getContent()).isEqualTo("Message 15");
		assertThat(result).hasSize(11);
		assertThat(result.get(0).getContent()).isEqualTo("Message 15");
	}
}
