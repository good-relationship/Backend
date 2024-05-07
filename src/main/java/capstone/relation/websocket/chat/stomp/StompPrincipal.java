<<<<<<<< HEAD:src/main/java/capstone/relation/websocket/chat/StompPrincipal.java
package capstone.relation.websocket.chat;
========
package capstone.relation.websocket.stomp;
>>>>>>>> 8323bac8933232be621dd5549580fa8fe90bf58f:src/main/java/capstone/relation/websocket/chat/stomp/StompPrincipal.java

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
