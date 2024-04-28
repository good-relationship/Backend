package capstone.relation.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.user.UserService;
import capstone.relation.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/info")
	public UserInfoDto getInfo() {
		return userService.getUserInfo();
	}
}
