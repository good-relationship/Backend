package capstone.relation.meeting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.api.auth.exception.AuthException;
import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.dto.response.JoinResponseDto;
import capstone.relation.meeting.dto.response.MeetingRoomDto;
import capstone.relation.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.meeting.repository.MeetRoomRepository;
import capstone.relation.user.dto.RoomInfoDto;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.SocketRegistry;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetRoomService {
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
	public JoinResponseDto createAndJoinRoom(CreateRoomDto createRoomDto, Long userId, String workSpaceId) {
		String roomName = createRoomDto.getRoomName();
		if (roomName == null || roomName.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "회의실 이름을 입력해주세요.");
		}
		try {
			JoinResponseDto joinResponseDto = createAndJoin(workSpaceId, userId.toString(), roomName);
			sendRoomList(workSpaceId);
			return joinResponseDto;
		} catch (AuthException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public boolean isUserInRoom(String userId) {
		return userRoomMapping.get(USER_KEY, userId) != null;
	}

	public RoomInfoDto getRoomInfo(String workspaceId, Long userId) {
		if (!isUserInRoom(userId.toString())) {
			return new RoomInfoDto();
		}
		String roomId = userRoomMapping.get(USER_KEY, userId.toString());
		System.out.println("roomId = " + roomId);
		System.out.println("userId = " + userId);
		Set<String> userIds = getRoomMembers(workspaceId, Long.parseLong(roomId));
		List<UserInfoDto> userInfoList = new ArrayList<>();
		for (String id : userIds) {
			UserInfoDto userInfoDto = new UserInfoDto();
			userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
			userInfoList.add(userInfoDto);
		}
		return new RoomInfoDto(true, Long.parseLong(roomId),
			meetRoomRepository.findById(Long.parseLong(roomId)).get().getRoomName(), userInfoList);
	}

	private JoinResponseDto createAndJoin(String workSpaceId, String userId, String roomName) {
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid workspace ID"));
		if (isUserInRoom(userId)) {
			throw new IllegalArgumentException("User is already in the room: " + userId);
		}

		MeetRoom meetRoom = MeetRoom.builder()
			.roomName(roomName)
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom);
		workSpace.addMeetRoom(meetRoom);
		Long roomId = meetRoom.getRoomId();
		return joinWorkspaceRoom(workSpaceId, userId, roomId);
	}

	public JoinResponseDto joinRoom(Long userId, String workspaceId, Long roomId) {

		//유저가 이미 회의 방에 있는지 확인
		String meetRoom = userRoomMapping.get(USER_KEY, userId.toString());
		if (meetRoom != null) {
			throw new IllegalArgumentException("User is already in the room: " + userId);
		}
		JoinResponseDto joinResponseDto = joinWorkspaceRoom(workspaceId, userId.toString(), roomId);
		sendRoomList(workspaceId);
		return joinResponseDto;
	}

	private JoinResponseDto joinWorkspaceRoom(String workSpaceId, String userId, Long roomId) {
		MeetRoom meetRoom = meetRoomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
		Set<String> userIds = addUserToRoom(workSpaceId, roomId, userId);
		List<UserInfoDto> userInfoList = new ArrayList<>();
		for (String id : userIds) {
			UserInfoDto userInfoDto = new UserInfoDto();
			userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
			userInfoList.add(userInfoDto);
		}
		sendUserList(workSpaceId, roomId);
		return new JoinResponseDto(roomId, meetRoom.getRoomName(), userInfoList, (long)userIds.size());
	}

	@Transactional(readOnly = false)
	public void leaveRoom(Long userId, String workspaceId) {
		String meetRoom = userRoomMapping.get(USER_KEY, userId.toString());
		if (meetRoom == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not in any room");
		}
		removeUserFromRoom(workspaceId, Long.parseLong(meetRoom), userId.toString());
		sendUserList(workspaceId, Long.parseLong(meetRoom));
		sendRoomList(workspaceId);
	}

	private Set<String> addUserToRoom(String workspaceId, Long roomId, String userId) {
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		if (roomParticipants == null) {
			roomParticipants = new HashMap<>();
		}
		Set<String> users = roomParticipants.get(roomId.toString());
		if (users == null) {
			users = new HashSet<>();
		}
		users.add(userId);
		roomParticipants.put(roomId.toString(), users);
		workspaceRoomParticipants.put(WORK_KEY, workspaceId, roomParticipants);
		userRoomMapping.put(USER_KEY, userId, roomId.toString());
		return users;
	}

	private void removeUserFromRoom(String workspaceId, Long roomId, String userId) {
		userRoomMapping.delete(USER_KEY, userId);
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId);
		Set<String> userIds = roomParticipants.get(roomId.toString());
		userIds.remove(userId);

		if (userIds.isEmpty()) {
			MeetRoom meetRoom = meetRoomRepository.findById(roomId).orElse(null);
			if (meetRoom != null) {
				meetRoom.setDeleted(true);
				meetRoomRepository.save(meetRoom);
			}
			roomParticipants.remove(roomId.toString());
		} else {
			roomParticipants.put(roomId.toString(), userIds);
		}
		if (roomParticipants.isEmpty()) {
			workspaceRoomParticipants.delete(WORK_KEY, workspaceId);
		} else {
			workspaceRoomParticipants.put(WORK_KEY, workspaceId, roomParticipants);
		}
	}

	public Set<String> getRoomMembers(String workspaceId, Long roomId) {
		HashMap<String, Set<String>> roomParticipants = workspaceRoomParticipants.get(WORK_KEY, workspaceId.toString());
		return roomParticipants.get(roomId.toString());
	}

	public void sendRoomList(String workSpaceId) {
		Set<MeetRoom> meetRooms = meetRoomRepository.findAllByWorkSpaceId(workSpaceId);
		MeetingRoomListDto meetingRoomListDto = new MeetingRoomListDto();
		for (MeetRoom meetRoom : meetRooms) {
			Long roomId = meetRoom.getRoomId();
			MeetingRoomDto meetingRoomDto = new MeetingRoomDto();
			meetingRoomDto.setRoomId(roomId);
			meetingRoomDto.setRoomName(meetRoom.getRoomName());
			Set<String> roomMembers = getRoomMembers(workSpaceId, roomId);
			meetingRoomDto.setUserCount(roomMembers.size());
			List<UserInfoDto> userInfoList = new ArrayList<>();
			for (String userId : roomMembers) {
				UserInfoDto userInfoDto = new UserInfoDto();
				userRepository.findById(Long.parseLong(userId)).ifPresent(userInfoDto::setByUserEntity);
				userInfoList.add(userInfoDto);
			}
			meetingRoomDto.setUserInfoList(userInfoList);
			meetingRoomListDto.getMeetingRoomList().add(meetingRoomDto);
		}
		simpMessagingTemplate.convertAndSend("/topic/" + workSpaceId + "/meetingRoomList", meetingRoomListDto);
	}

	private void sendUserList(String workSpaceId, Long roomId) {
		Set<String> userIds = getRoomMembers(workSpaceId, roomId);
		List<UserInfoDto> userInfoList = new ArrayList<>();
		for (String userId : userIds) {
			UserInfoDto userInfoDto = new UserInfoDto();
			userRepository.findById(Long.parseLong(userId)).ifPresent(userInfoDto::setByUserEntity);
			userInfoList.add(userInfoDto);
		}
		simpMessagingTemplate.convertAndSend("/topic/meetingRoom/" + roomId + "/users", userInfoList);
	}

	public void sendErrorMessage(SimpMessageHeaderAccessor headerAccessor, String message, String destination,
		int status) {
		Long userId = (Long)headerAccessor.getSessionAttributes().get("userId");
		String socketId = socketRegistry.getSocketId(userId.toString());
		simpMessagingTemplate.convertAndSendToUser(socketId, destination,
			ResponseEntity.status(status).body(message));
	}
}
