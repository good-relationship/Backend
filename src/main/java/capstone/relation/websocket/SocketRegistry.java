package capstone.relation.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SocketRegistry {
	private final Map<String, String> userIdToSocketIdMap = new ConcurrentHashMap<>();

	public void registerSession(String userId, String socketId) {
		userIdToSocketIdMap.put(userId, socketId);
	}

	public String getSocketId(String userId) {
		return userIdToSocketIdMap.get(userId);
	}

	public void unregisterSession(String userId) {
		userIdToSocketIdMap.remove(userId);
	}
}