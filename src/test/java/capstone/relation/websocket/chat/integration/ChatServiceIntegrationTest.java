package capstone.relation.websocket.chat.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.user.domain.Role;
import capstone.relation.user.domain.User;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.chat.ChatService;
import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.ChatRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.exception.WorkSpaceException;
import capstone.relation.workspace.repository.WorkSpaceRepository;

@SpringBootTest
@Transactional
class ChatServiceIntegrationTest {

	@Autowired
	private ChatService chatService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WorkSpaceRepository workSpaceRepository;

	@Autowired
	private ChatRepository chatRepository;

	private User user;
	private WorkSpace workSpace;
	private SimpMessageHeaderAccessor headerAccessor;

	@BeforeEach
	void setUp() {
		// Given: 사용자와 작업 공간이 주어졌을 때
		user = User.builder()
			.email("example@example.com")
			.userName("example")
			.profileImage("example.jpg")
			.provider("kakao")
			.role(Role.USER)
			.build();
		workSpace = new WorkSpace();

		userRepository.save(user);
		workSpaceRepository.save(workSpace);

		// 사용자와 워크스페이스 간의 관계를 설정합니다.
		user.setWorkSpace(workSpace);

		// SimpMessageHeaderAccessor를 설정합니다.
		headerAccessor = SimpMessageHeaderAccessor.create();
		Map<String, Object> sessionAttributes = new HashMap<>();
		sessionAttributes.put("userId", user.getId());
		headerAccessor.setSessionAttributes(sessionAttributes);
	}

	@Test
	@DisplayName("통합 테스트 : 새로운 메시지를 전송할 수 있다.")
	void testSendNewMessage() {
		// Given: 유효한 content가 주어졌을 때
		String content = "Test message";

		// When: 사용자가 새로운 메시지를 전송하면
		MessageDto messageDto = chatService.sendNewMessage(workSpace.getId(), content, headerAccessor);

		// Then: 메시지가 올바르게 전송되고 저장되어야 한다.
		assertNotNull(messageDto);
		assertEquals(content, messageDto.getContent());
		assertEquals(user.getId().toString(), messageDto.getSender().getSenderId());

		// 그리고 채팅이 실제로 데이터베이스에 저장되어야 한다.
		Chat chat = chatRepository.findById(messageDto.getMessageId()).orElse(null);
		assertNotNull(chat);
		assertEquals(content, chat.getContent());
		assertEquals(user, chat.getSender());
		assertEquals(workSpace, chat.getWorkSpace());
	}

	@Test
	@DisplayName("통합 테스트 : 유효하지 않은 사용자로 메시지를 전송하면 예외가 발생해야 한다.")
	void testSendNewMessage_InvalidUser() {
		// Given: 유효하지 않은 사용자 ID가 주어졌을 때
		headerAccessor.getSessionAttributes().put("userId", -1L);

		// When: 사용자가 메시지를 전송하려고 하면
		String content = "Test message";

		// Then:
		assertThrows(AuthException.class, () -> {
			chatService.sendNewMessage(workSpace.getId(), content, headerAccessor);
		});
	}

	@Test
	@DisplayName("통합 테스트 : 유효하지 않은 워크스페이스로 메시지를 전송하면 예외가 발생해야 한다.")
	void testSendNewMessage_InvalidWorkspace() {
		// Given: 유효하지 않은 워크스페이스 ID가 주어졌을 때
		String invalidWorkSpaceId = "invalid-workspace-id";

		// When: 사용자가 메시지를 전송하려고 하면
		String content = "Test message";

		// Then: WorkspaceException이 발생해야 한다.
		assertThrows(WorkSpaceException.class, () -> {
			chatService.sendNewMessage(invalidWorkSpaceId, content, headerAccessor);
		});
	}
}
