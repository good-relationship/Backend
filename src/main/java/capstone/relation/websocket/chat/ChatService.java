package capstone.relation.websocket.chat;

import java.time.LocalDateTime;
import java.util.Collections;
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
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.ChatRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.exception.WorkspaceErrorCode;
import capstone.relation.workspace.exception.WorkspaceException;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final WorkSpaceRepository workSpaceRepository;

	@Transactional(readOnly = false)
	public MessageDto sendNewMessage(String workSpaceId, String content, SimpMessageHeaderAccessor headerAccessor) {
		if (content == null || content.isEmpty()) {
			return null;
		}
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		Long userId = (Long)sessionAttributes.get("userId");
		System.out.println("userId: " + userId);
		if (userId == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		User user = userRepository.findById(userId).orElse(null);
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElse(null);
		if (user == null || workSpace == null || user.getWorkSpace() != workSpace) {
			throw new WorkspaceException(WorkspaceErrorCode.INVALID_ACCESS);
		}
		Chat chat = new Chat(user, workSpace, content, LocalDateTime.now());
		chatRepository.save(chat);
		return ChatMapper.INSTANCE.chatToMessageDto(chat);
	}

	public HistoryResponseDto getRecentHistory(SimpMessageHeaderAccessor headerAccessor) {
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		Long userId = (Long)sessionAttributes.get("userId");
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		System.out.println("workSpace: " + workSpace);
		List<Chat> chats = chatRepository.findTop10ByWorkSpaceOrderByIdDesc(workSpace);
		System.out.println("chats: " + chats);
		HistoryResponseDto historyResponseDto = new HistoryResponseDto();
		historyResponseDto.setStart(true);
		if (chats.isEmpty()) {
			return historyResponseDto;
		}
		historyResponseDto.setMessages(ChatMapper.INSTANCE.chatToMessageDtoList(chats));
		historyResponseDto.setEnd(chats.size() < 10);
		historyResponseDto.setLastMsgId(chats.get(0).getId());
		return historyResponseDto;
	}

	public HistoryResponseDto getHistory(Long lastMsgId, SimpMessageHeaderAccessor headerAccessor) {
		System.out.println("lastMsgId: " + lastMsgId);
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		Long userId = (Long)sessionAttributes.get("userId");
		boolean isStart = false;
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
		List<Chat> chats;
		if (lastMsgId == null || lastMsgId == 0L) {
			chats = chatRepository.findTop10ByWorkSpaceOrderByIdDesc(workSpace);
			isStart = true;
		} else {
			chats = chatRepository.findTop10ByWorkSpaceAndIdIsLessThanOrderByIdDesc(workSpace,
				lastMsgId);
		}
		System.out.println("chats: " + chats);
		HistoryResponseDto historyResponseDto = new HistoryResponseDto();
		historyResponseDto.setStart(isStart);
		if (chats.isEmpty()) {
			return historyResponseDto;
		}
		Collections.reverse(chats);
		historyResponseDto.setMessages(ChatMapper.INSTANCE.chatToMessageDtoList(chats));
		historyResponseDto.setEnd(chats.size() < 10);
		historyResponseDto.setLastMsgId(chats.get(0).getId());

		return historyResponseDto;
	}
}




