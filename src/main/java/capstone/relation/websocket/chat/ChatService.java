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
import capstone.relation.user.exception.UserErrorCode;
import capstone.relation.user.exception.UserException;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.websocket.chat.dto.response.HistoryResponseDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;
import capstone.relation.websocket.chat.repository.ChatRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.exception.WorkSpaceErrorCode;
import capstone.relation.workspace.exception.WorkSpaceException;
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
		if (content == null || content.isEmpty())
			return null;

		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		Long userId = (Long)sessionAttributes.get("userId");
		if (userId == null)
			throw new AuthException(AuthErrorCode.INVALID_TOKEN);
		User user = userRepository.findById(userId).orElseThrow(() ->
			new AuthException(AuthErrorCode.INVALID_TOKEN));
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElse(null);
		if (user == null || workSpace == null || user.getWorkSpace() != workSpace)
			throw new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE);

		Chat chat = new Chat(user, workSpace, content, LocalDateTime.now());
		chatRepository.save(chat);
		return ChatMapper.INSTANCE.chatToMessageDto(chat);
	}

	/**
	 * 채팅방의 메시지 목록을 반환한다.(10개) 마지막 메시지 ID가 주어지면 해당 ID 이후의 메시지 목록을 반환한다.
	 * @param lastMsgId 마지막 메시지 ID
	 * @param userId 사용자 ID
	 * @return 메시지 목록 응답 DTO(HistoryResponseDto)
	 */
	public HistoryResponseDto getHistory(Long lastMsgId, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() ->
			new UserException(UserErrorCode.INVALID_USER));

		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null)
			throw new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE);

		List<Chat> chats = retrieveChats(workSpace, lastMsgId);
		HistoryResponseDto historyResponseDto = HistoryResponseDto.builder()
			.isStart(lastMsgId == null || lastMsgId == 0L)
			.isEnd(chats.size() <= 10)
			.build();
		//가장 마지막 메시지를 없앤다.
		if (chats.size() == 11)
			chats.remove(10);
		if (!chats.isEmpty()) {
			Collections.reverse(chats);
			historyResponseDto.setMessages(ChatMapper.INSTANCE.chatToMessageDtoList(chats));
			historyResponseDto.setLastMsgId(chats.get(0).getId());
		}
		return historyResponseDto;
	}

	private List<Chat> retrieveChats(WorkSpace workSpace, Long lastMsgId) {
		if (lastMsgId == null || lastMsgId == 0L) {
			return chatRepository.findTop11ByWorkSpaceOrderByIdDesc(workSpace);
		} else {
			return chatRepository.findTop11ByWorkSpaceAndIdIsLessThanOrderByIdDesc(workSpace, lastMsgId);
		}
	}
}




