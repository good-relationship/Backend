package capstone.relation.websocket.chat;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.user.domain.User;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.ChatRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final WorkSpaceRepository workSpaceRepository;

	//TODO : Exception 클래스 만들어 처리
	@Transactional(readOnly = false)
	public MessageDto sendNewMessage(String workSpaceId, String content, SimpMessageHeaderAccessor headerAccessor) {
		if (content == null || content.isEmpty())
			return null;
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		Long userId = (Long)sessionAttributes.get("userId");
		String userName = (String)sessionAttributes.get("userName");
		if (userId == null || userName == null)
			return null;
		User user = userRepository.findById(userId).orElse(null);
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElse(null);
		if (user == null || workSpace == null || user.getWorkSpace() != workSpace) {
			return null;
		}
		Chat chat = new Chat(user, workSpace, content, LocalDateTime.now());
		chatRepository.save(chat);
		return ChatMapper.INSTANCE.chatToMessageDto(chat);
	}
}




