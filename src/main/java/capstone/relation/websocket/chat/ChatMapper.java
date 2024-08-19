package capstone.relation.websocket.chat;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import capstone.relation.user.domain.User;
import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.websocket.chat.dto.SenderDto;
import capstone.relation.websocket.chat.dto.response.MessageDto;

@Mapper
public interface ChatMapper {
	ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

	@Mappings({
		@Mapping(target = "messageId", source = "id"),
		@Mapping(target = "sender", source = "sender", qualifiedByName = "userToSenderDto"),
		@Mapping(target = "time", source = "timestamp"),
		@Mapping(target = "content", source = "content")
	})
	MessageDto chatToMessageDto(Chat chat);

	List<MessageDto> chatToMessageDtoList(List<Chat> chats);
	
	@Named("userToSenderDto")
	default SenderDto userToSenderDto(User user) {
		if (user == null) {
			return null;
		}
		SenderDto dto = new SenderDto();
		dto.setSenderId(user.getId().toString());
		dto.setSenderName(user.getUserName());
		dto.setSenderImage(user.getProfileImage());
		return dto;
	}
}
