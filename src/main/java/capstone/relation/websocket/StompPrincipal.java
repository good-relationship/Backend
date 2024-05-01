package capstone.relation.websocket;

import java.security.Principal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class StompPrincipal implements Principal {
	private String name; //socketId (UUID)로 사용

	StompPrincipal(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
