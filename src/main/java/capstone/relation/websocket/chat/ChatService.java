package capstone.relation.websocket.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.api.auth.exception.AuthErrorCode;
import capstone.relation.api.auth.exception.AuthException;
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
		if (userId == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		User user = userRepository.findById(userId).orElse(null);
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElse(null);
		if (user == null || workSpace == null || user.getWorkSpace() != workSpace) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		Chat chat = new Chat(user, workSpace, content, LocalDateTime.now());
		chatRepository.save(chat);
		return ChatMapper.INSTANCE.chatToMessageDto(chat);
	}

	//TODO : Exception 클래스 만들어 처리
	public List<MessageDto> getHistory(Long lastMsgId, SimpMessageHeaderAccessor headerAccessor) {
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		Long userId = (Long)sessionAttributes.get("userId");
		User user = userRepository.findById(userId).orElse(null);
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null)
			return null;
		List<Chat> chats = chatRepository.findTop10ByWorkSpaceAndIdLessThanOrderByIdDesc(workSpace,
			lastMsgId);
		return ChatMapper.INSTANCE.chatToMessageDtoList(chats);
	}
}




