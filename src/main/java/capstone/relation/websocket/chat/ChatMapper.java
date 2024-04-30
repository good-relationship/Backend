package capstone.relation.websocket.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
		@Mapping(target = "time", source = "timestamp", qualifiedByName = "localDateTimeToString"),
		@Mapping(target = "content", source = "content")
	})
	MessageDto chatToMessageDto(Chat chat);

	@Named("localDateTimeToString")
	default String localDateTimeToString(LocalDateTime localDateTime) {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
	}

	@Named("userToSenderDto")
	default SenderDto userToSenderDto(User user) {
		if (user == null)
			return null;
		SenderDto dto = new SenderDto();
		dto.setSenderId(user.getId().toString());
		dto.setSenderName(user.getUserName());
		dto.setSenderImage(user.getProfileImage());
		return dto;
	}
}
