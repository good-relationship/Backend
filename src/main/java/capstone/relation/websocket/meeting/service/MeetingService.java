package capstone.relation.websocket.meeting.service;

import java.util.HashSet;
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
	private static final String KEY = "MeetingRoomUsers";
	private final UserRepository userRepository;
	private final WorkSpaceRepository workSpaceRepository;
	private final MeetRoomRepository meetRoomRepository;
	private final SocketRegistry socketRegistry;
	private final RedisTemplate<String, Object> redisTemplate;
	private HashOperations<String, String, Set<String>> hashOperations;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@PostConstruct
	protected void init() {
		hashOperations = redisTemplate.opsForHash();
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
		Set<String> userIds = addUserToRoom(roomId, userId);
		Set<UserInfoDto> userInfoList = new HashSet<>();
		for (String id : userIds) {
			UserInfoDto userInfoDto = new UserInfoDto();
			userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
			userInfoList.add(userInfoDto);
		}
		return new JoinResponseDto(roomId, roomName, userInfoList, (long)userIds.size());
	}

	public Set<String> addUserToRoom(Long roomId, String userId) {
		Set<String> userIds = hashOperations.get(KEY, roomId.toString());
		if (userIds == null) {
			userIds = new HashSet<>();
		}
		userIds.add(userId);
		hashOperations.put(KEY, roomId.toString(), userIds);
		return userIds;
	}

	public void removeUserFromRoom(Long roomId, String userId) {
		Set<String> userIds = hashOperations.get(KEY, roomId.toString());
		userIds.remove(userId);
		hashOperations.put(KEY, roomId.toString(), userIds);
	}

	public Set<String> getRoomMembers(Long roomId) {
		return hashOperations.get(KEY, roomId.toString());
	}

	public MeetingRoomListDto getRoomList(String workSpaceId) {
		// Set<String> roomList = meetingRoomRepository.getRoomList(workSpaceId);
		Set<MeetRoom> meetRooms = meetRoomRepository.findAllByWorkSpaceId(workSpaceId);
		MeetingRoomListDto meetingRoomListDto = new MeetingRoomListDto();
		for (MeetRoom meetRoom : meetRooms) {
			MeetingRoomDto meetingRoomDto = new MeetingRoomDto();
			meetingRoomDto.setRoomId(meetRoom.getRoomId());
			meetingRoomDto.setRoomName(meetRoom.getRoomName());
			meetingRoomDto.setUserCount(getRoomMembers(meetRoom.getRoomId()).size());
			meetingRoomListDto.getMeetingRoomList().add(meetingRoomDto);
		}
		return meetingRoomListDto;
	}

	private void sendErrorMessage(String socketId, String message, String destination, int status) {
		simpMessagingTemplate.convertAndSendToUser(socketId, destination,
			ResponseEntity.status(status).body(message));
	}

	// public JoinResponseDto joinRoom(Map<String, Object> sessionAttributes, String roomId) {
	// 	String userId = sessionAttributes.get("userId").toString();
	// 	if (userId == null) { //TODO: exception
	// 		throw new IllegalArgumentException("User does not exist: " + userId);
	// 	}
	// 	if (roomId == null) { //TODO: exception
	// 		throw new IllegalArgumentException("Room does not exist: " + roomId);
	// 	}
	// 	if (meetingRoomRepository.isUserInRoom(roomId, userId)) {
	// 		throw new IllegalArgumentException("User is already in the room: " + userId);
	// 	}
	// 	Set<String> userIds = meetingRoomRepository.enterRoom(roomId, userId);
	// 	Set<UserInfoDto> userInfoList = new HashSet<>();
	// 	for (String id : userIds) {
	// 		UserInfoDto userInfoDto = new UserInfoDto();
	// 		userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
	// 		userInfoList.add(userInfoDto);
	// 	}
	// 	return new JoinResponseDto(roomId, meetingRoomRepository.getRoomName(roomId), userInfoList,
	// 		(long)userIds.size());
	// }
	//
	// public String getRoomName(String roomId) {
	// 	return meetingRoomRepository.getRoomName(roomId);
	// }
}
