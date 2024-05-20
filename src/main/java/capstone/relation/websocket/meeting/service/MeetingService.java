package capstone.relation.websocket.meeting.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.websocket.meeting.domain.MeetRoom;
import capstone.relation.websocket.meeting.dto.request.CreateRoomDto;
import capstone.relation.websocket.meeting.dto.response.JoinResponseDto;
import capstone.relation.websocket.meeting.dto.response.MeetingRoomDto;
import capstone.relation.websocket.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.websocket.meeting.repository.MeetRoomRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {
	private static final String WORK_KEY = "WORKSPACE_ROOM_PARTICIPANTS";
	private static final String USER_KEY = "USER_ROOM_MAPPING";
	private final UserRepository userRepository;
	private final WorkSpaceRepository workSpaceRepository;
	private final MeetRoomRepository meetRoomRepository;
	private final SocketRegistry socketRegistry;
	private final RedisTemplate<String, Object> redisTemplate;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private HashOperations<String, String, HashMap<String, Set<String>>> workspaceRoomParticipants;
	//workspaceId, roomId, userIds
	private HashOperations<String, String, String> userRoomMapping;

	@PostConstruct
	protected void init() {
		workspaceRoomParticipants = redisTemplate.opsForHash();
		userRoomMapping = redisTemplate.opsForHash();
	}

	@Transactional(readOnly = false)
	public void createRoom(CreateRoomDto createRoomDto, SimpMessageHeaderAccessor headerAccessor) {
		String roomName = createRoomDto.getRoomName();
		Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
		String socketId = socketRegistry.getSocketId(userId.toString());
		if (roomName == null || roomName.isEmpty()) {
			sendErrorMessage(socketId, "회의실 이름을 입력해주세요.", "/queue/join", 401);
		}
		try {
			String workSpaceId = (String)headerAccessor.getSessionAttributes().get("workSpaceId");
			JoinResponseDto joinResponseDto = createAndJoin(workSpaceId, userId.toString(), roomName);
			MeetingRoomListDto roomList = getRoomList(workSpaceId);
			simpMessagingTemplate.convertAndSend("/topic/" + workSpaceId + "/meetingRoomList", roomList);
			simpMessagingTemplate.convertAndSendToUser(socketId, "/queue/join", joinResponseDto);
		} catch (AuthException e) {
			e.printStackTrace();
			sendErrorMessage(socketId, e.getMessage(), "/queue/join", 401);
		} catch (Exception e) {
			e.printStackTrace();
			sendErrorMessage(socketId, "서버 내부 오류가 발생했습니다.", "/queue/join", 500);
		}
	}

	private JoinResponseDto createAndJoin(String workSpaceId, String userId, String roomName) {
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid workspace ID"));
		MeetRoom meetRoom = MeetRoom.builder()
			.roomName(roomName)
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom);
		workSpace.addMeetRoom(meetRoom);
		Long roomId = meetRoom.getRoomId();
		Set<String> userIds = addUserToRoom(workSpaceId, roomId, userId);
		Set<UserInfoDto> userInfoList = new HashSet<>();
		for (String id : userIds) {
			UserInfoDto userInfoDto = new UserInfoDto();
			userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
			userInfoList.add(userInfoDto);
		}
		return new JoinResponseDto(roomId, roomName, userInfoList, (long)userIds.size());
	}

	public Set<String> addUserToRoom(String workspaceId, Long roomId, String userId) {
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		Set<String> userIds = new HashSet<>();
		if (roomParticipants == null) {
			roomParticipants = new HashMap<>();
		} else {
			userIds.add(userId);
		}
		Set<String> users = roomParticipants.get(roomId.toString());
		if (users == null) {
			users = new HashSet<>();
		}
		users.add(userId);
		roomParticipants.put(roomId.toString(), users);
		workspaceRoomParticipants.put(WORK_KEY, workspaceId, roomParticipants);
		userRoomMapping.put(USER_KEY, userId, roomId.toString());
		return userIds;
	}

	public void removeUserFromRoom(String workspaceId, Long roomId, String userId) {
		userRoomMapping.delete(USER_KEY, userId);
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		Set<String> userIds = roomParticipants.get(roomId.toString());
		userIds.remove(userId);
		roomParticipants.put(roomId.toString(), userIds);
		workspaceRoomParticipants.put(WORK_KEY, workspaceId, roomParticipants);
	}

	public Set<String> getRoomMembers(String workspaceId, Long roomId) {
		System.out.println("workspaceId = " + workspaceId);
		HashMap<String, Set<String>> stringSetHashMap = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		return stringSetHashMap.get(roomId.toString());
	}

	public MeetingRoomListDto getRoomList(String workSpaceId) {
		// Set<String> roomList = meetingRoomRepository.getRoomList(workSpaceId);
		Set<MeetRoom> meetRooms = meetRoomRepository.findAllByWorkSpaceId(workSpaceId);
		MeetingRoomListDto meetingRoomListDto = new MeetingRoomListDto();
		System.out.println("workSpaceId = " + workSpaceId);
		for (MeetRoom meetRoom : meetRooms) {
			System.out.println("meetRoom.getRoomId() = " + meetRoom.getRoomId());
			MeetingRoomDto meetingRoomDto = new MeetingRoomDto();
			meetingRoomDto.setRoomId(meetRoom.getRoomId());
			meetingRoomDto.setRoomName(meetRoom.getRoomName());
			meetingRoomDto.setUserCount(getRoomMembers(workSpaceId, meetRoom.getRoomId()).size());
			meetingRoomListDto.getMeetingRoomList().add(meetingRoomDto);
		}
		return meetingRoomListDto;
	}

	private void sendErrorMessage(String socketId, String message, String destination, int status) {
		simpMessagingTemplate.convertAndSendToUser(socketId, destination,
			ResponseEntity.status(status).body(message));
	}

	public JoinResponseDto joinRoom(Map<String, Object> sessionAttributes, String roomId) {
		String userId = sessionAttributes.get("userId").toString();
		if (userId == null) { //TODO: exception
			throw new IllegalArgumentException("User does not exist: " + userId);
		}
		if (roomId == null) { //TODO: exception
			throw new IllegalArgumentException("Room does not exist: " + roomId);
		}
		//유저가 이미 회의 방에 있는지 확인
		String s = userRoomMapping.get(USER_KEY, userId);
		if (s != null) {
			throw new IllegalArgumentException("User is already in the room: " + userId);
		}
		// Set<String> userIds = meetingRoomRepository.enterRoom(roomId, userId);
		// Set<UserInfoDto> userInfoList = new HashSet<>();
		// for (String id : userIds) {
		// 	UserInfoDto userInfoDto = new UserInfoDto();
		// 	userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
		// 	userInfoList.add(userInfoDto);
		// }
		// return new JoinResponseDto(roomId, meetingRoomRepository.getRoomName(roomId), userInfoList,
		// 	(long)userIds.size());
		return null;
	}

}
