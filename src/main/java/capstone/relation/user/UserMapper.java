package capstone.relation.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import capstone.relation.user.domain.User;
import capstone.relation.user.dto.UserInfoDto;

@Mapper
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	@Mapping(target = "userId", source = "id")
	@Mapping(target = "userName", source = "userName")
	@Mapping(target = "userImage", source = "profileImage")
	UserInfoDto toUserInfoDto(User user);
}
