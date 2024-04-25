package capstone.relation.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.user.dto.UserInfo;

@RestController
@RequestMapping("/user")
public class UserController {
	@GetMapping("/info")
	public UserInfo getInfo() {
		UserInfo userInfo = new UserInfo();
		userInfo.setDummy();
		return userInfo;
	}
}
