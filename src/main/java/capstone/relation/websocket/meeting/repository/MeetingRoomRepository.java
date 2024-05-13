package capstone.relation.websocket.meeting.repository;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingRoomRepository {
	private final RedisTemplate<String, Object> redisTemplate;

	public MeetingRoomRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public String createRoom(String workspaceId, String roomName) {
		String roomId = UUID.randomUUID().toString();
		HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
		hashOps.put("room:" + roomId, "roomName", roomName);
		hashOps.put("room:" + roomId, "workSpaceId", roomId);
		redisTemplate.opsForSet().add("workspace:" + workspaceId + ":rooms", roomId);
		return roomId;
	}

	public Set<String> enterRoom(String roomId, String userId) {
		Boolean roomExists = redisTemplate.hasKey("room:" + roomId);
		if (Boolean.TRUE.equals(roomExists)) {
			redisTemplate.opsForSet().add("room:" + roomId + ":members", userId);
		} else { //TODO: throw exception
			throw new IllegalArgumentException("Room does not exist: " + roomId);
		}
		return getRoomMembers(roomId);
	}

	public Set<String> getRoomList(String workspaceId) {
		SetOperations<String, Object> setOps = redisTemplate.opsForSet();
		Set<Object> members = setOps.members("workspace:" + workspaceId + ":rooms");
		Set<String> roomIds = members.stream()
			.filter(Objects::nonNull)
			.map(Object::toString)
			.collect(Collectors.toSet());
		System.out.println("members::" + members);
		return roomIds;
		// return redisTemplate.keys("workspace:" + workspaceId + ":rooms");
	}

	public String getRoomName(String roomId) {
		return (String)redisTemplate.opsForHash().get("room:" + roomId, "roomName");
	}

	public Set<String> getRoomMembers(String roomId) {
		Set<Object> members = redisTemplate.opsForSet().members("room:" + roomId + ":members");
		return members.stream()
			.filter(Objects::nonNull)
			.map(Object::toString)
			.collect(Collectors.toSet());
	}

	public long getRoomUserCount(String roomId) {
		return redisTemplate.opsForSet().size("room:" + roomId + ":members");
	}

	public String getWorkspaceId(String roomId) {
		return (String)redisTemplate.opsForHash().get("room:" + roomId, "workspaceId");
	}

	public void deleteRoom(String roomId) {
		redisTemplate.delete("room:" + roomId);
	}

	public void leaveRoom(String roomId, String userId) {
		redisTemplate.opsForSet().remove("room:" + roomId + ":members", userId);
	}

	public boolean isRoomExist(String roomId) {
		return redisTemplate.hasKey("room:" + roomId);
	}

	public boolean isUserInRoom(String roomId, String userId) {
		return redisTemplate.opsForSet().isMember("room:" + roomId + ":members", userId);
	}

	public void deleteAllRooms() {
		redisTemplate.delete(redisTemplate.keys("rooms:*"));
	}

}
