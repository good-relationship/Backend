package capstone.relation.meeting.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
	private static final String WORK_KEY = "WORKSPACE_ROOM_PARTICIPANTS";
	private static final String USER_KEY = "USER_ROOM_MAPPING";
	private final RedisTemplate<String, Object> redisTemplate;
	private HashOperations<String, String, HashMap<String, Set<String>>> workspaceRoomParticipants;
	private HashOperations<String, String, String> userRoomMapping;

	@PostConstruct
	protected void init() {
		workspaceRoomParticipants = redisTemplate.opsForHash();
		userRoomMapping = redisTemplate.opsForHash();
	}

	public boolean isUserInRoom(Long userId) {
		return userRoomMapping.get(USER_KEY, userId.toString()) != null;
	}

	public String getUserRoomId(Long userId) {
		return userRoomMapping.get(USER_KEY, userId.toString());
	}

	public void addUserToRoom(String workspaceId, Long roomId, String userId) {
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		if (roomParticipants == null)
			roomParticipants = new HashMap<>();
		Set<String> users = roomParticipants.get(roomId.toString());
		if (users == null)
			users = new HashSet<>();

		users.add(userId);
		roomParticipants.put(roomId.toString(), users);
		workspaceRoomParticipants.put(WORK_KEY, workspaceId, roomParticipants);
		userRoomMapping.put(USER_KEY, userId, roomId.toString());
	}

	public void removeUserFromRoom(String workspaceId, Long roomId, String userId) {
		userRoomMapping.delete(USER_KEY, userId);
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		if (roomParticipants == null)
			return;
		Set<String> userIds = roomParticipants.get(roomId.toString());
		userIds.remove(userId);

		if (userIds.isEmpty())
			roomParticipants.remove(roomId.toString());
		else
			roomParticipants.put(roomId.toString(), userIds);

		if (roomParticipants.isEmpty())
			workspaceRoomParticipants.delete(WORK_KEY, workspaceId);
		else
			workspaceRoomParticipants.put(WORK_KEY, workspaceId, roomParticipants);

	}

	public Set<String> getRoomMemberIds(String workspaceId, Long roomId) {
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		if (roomParticipants == null) {
			return new HashSet<>();
		}
		Set<String> membersId = roomParticipants.get(roomId.toString());
		if (membersId == null) {
			return new HashSet<>();
		}
		return membersId;
	}

	public void deleteAll() {
		redisTemplate.delete(WORK_KEY);
		redisTemplate.delete(USER_KEY);
	}
}
