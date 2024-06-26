package capstone.relation.user;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.api.auth.jwt.SecurityUser;
import capstone.relation.user.domain.User;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public UserInfoDto getUserInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null
			&& authentication.getPrincipal() instanceof SecurityUser) {
			SecurityUser userDetails = (SecurityUser)authentication.getPrincipal();
			Long userId = userDetails.getUserId();
			Optional<User> userOptional = userRepository.findById(userId);
			if (userOptional.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
			}
			UserInfoDto userInfoDto = UserMapper.INSTANCE.toUserInfoDto(userOptional.get());
			return userInfoDto;
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
	}

	public UserInfoDto getUserInfo(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
		}
		UserInfoDto userInfoDto = UserMapper.INSTANCE.toUserInfoDto(userOptional.get());
		return userInfoDto;
	}

	public User getUserEntity() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null
			&& authentication.getPrincipal() instanceof SecurityUser) {
			SecurityUser userDetails = (SecurityUser)authentication.getPrincipal();
			Long userId = userDetails.getUserId();
			Optional<User> userOptional = userRepository.findById(userId);
			if (userOptional.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
			}
			return userOptional.get();
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
	}

	public void setInvitedWorkspaceId(String inviteWorkSpaceId) {
		User user = getUserEntity();
		user.setInvitedWorkspaceId(inviteWorkSpaceId);
		userRepository.save(user);
	}

	public void leaveWorkspace() {
		User user = getUserEntity();
		user.setWorkSpace(null);
		userRepository.save(user);
	}
}
