package capstone.relation.meeting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import capstone.relation.common.util.SecurityUtil;
import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.dto.response.JoinResponseDto;
import capstone.relation.meeting.dto.response.MeetingRoomDto;
import capstone.relation.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.meeting.repository.MeetRoomRepository;
import capstone.relation.meeting.repository.RedisRepository;
import capstone.relation.user.UserService;
import capstone.relation.user.dto.RoomInfoDto;
import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetRoomService {
	private final UserService userService;
	private final WorkSpaceRepository workSpaceRepository;
	private final MeetRoomRepository meetRoomRepository;
	private final RedisRepository redisRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;

	@Transactional(readOnly = false)
	public JoinResponseDto createAndJoinRoom(CreateRoomDto createRoomDto) {
		String roomName = createRoomDto.getRoomName();
		if (roomName == null || roomName.isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "회의실 이름을 입력해주세요.");
		Long userId = SecurityUtil.getCurrentUserId();
		String workSpaceId = userService.getUserWorkSpaceId(userId);
		JoinResponseDto joinResponseDto = createAndJoin(workSpaceId, userId, roomName);
		sendRoomList(workSpaceId);
		return joinResponseDto;
	}

	private JoinResponseDto createAndJoin(String workSpaceId, Long userId, String roomName) {
		WorkSpace workSpace = workSpaceRepository.findById(workSpaceId)
			.orElseThrow(() -> new IllegalArgumentException("유저의 워크스페이스 정보가 없습니다."));
		if (redisRepository.isUserInRoom(userId.toString()))
			throw new IllegalArgumentException("User is already in the room: " + userId);
		MeetRoom meetRoom = MeetRoom.builder()
			.roomName(roomName)
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom);
		workSpace.addMeetRoom(meetRoom);
		Long roomId = meetRoom.getRoomId();
		return joinWorkspaceRoom(workSpaceId, userId.toString(), roomId);
	}

	public RoomInfoDto getRoomInfo(String workspaceId, Long userId) {
		if (!redisRepository.isUserInRoom(userId.toString()))
			throw new IllegalArgumentException("User is not in any room: " + userId);

		String roomId = redisRepository.getUserRoomId(userId.toString());
		Set<String> userIds = redisRepository.getRoomMembers(workspaceId, Long.parseLong(roomId));
		List<UserInfoDto> userInfoList = userService.getUserInfoList(userIds);
		return new RoomInfoDto(true, Long.parseLong(roomId),
			meetRoomRepository.findById(Long.parseLong(roomId)).get().getRoomName(), userInfoList);
	}

	public JoinResponseDto joinRoom(Long roomId) {
		Long userId = SecurityUtil.getCurrentUserId();
		String workspaceId = userService.getUserWorkSpaceId(userId);
		String meetRoomId = redisRepository.getUserRoomId(userId.toString());
		if (meetRoomId != null)
			throw new IllegalArgumentException("User is already in the room: " + userId);

		JoinResponseDto joinResponseDto = joinWorkspaceRoom(workspaceId, userId.toString(), roomId);
		sendRoomList(workspaceId);
		return joinResponseDto;
	}

	private JoinResponseDto joinWorkspaceRoom(String workSpaceId, String userId, Long roomId) {
		MeetRoom meetRoom = meetRoomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
		redisRepository.addUserToRoom(workSpaceId, roomId, userId);
		Set<String> userIds = redisRepository.getRoomMembers(workSpaceId, roomId);
		List<UserInfoDto> userInfoList = userService.getUserInfoList(userIds);
		sendUserList(workSpaceId, roomId);
		return new JoinResponseDto(roomId, meetRoom.getRoomName(), userInfoList, (long)userIds.size());
	}

	@Transactional(readOnly = false)
	public void leaveRoom(Long userId) {
		String workspaceId = userService.getUserWorkSpaceId(userId);
		String meetRoom = redisRepository.getUserRoomId(userId.toString());
		if (meetRoom == null)
			return;
		redisRepository.removeUserFromRoom(workspaceId, Long.parseLong(meetRoom), userId.toString());
		sendUserList(workspaceId, Long.parseLong(meetRoom));
		sendRoomList(workspaceId);
	}

	public MeetingRoomListDto sendRoomList(String workSpaceId) {
		Set<MeetRoom> meetRooms = meetRoomRepository.findAllByWorkSpaceId(workSpaceId);
		MeetingRoomListDto meetingRoomListDto = new MeetingRoomListDto();
		for (MeetRoom meetRoom : meetRooms) {
			Long roomId = meetRoom.getRoomId();
			MeetingRoomDto meetingRoomDto = new MeetingRoomDto();
			meetingRoomDto.setRoomId(roomId);
			meetingRoomDto.setRoomName(meetRoom.getRoomName());
			Set<String> roomMembers = redisRepository.getRoomMembers(workSpaceId, roomId);
			if (roomMembers == null) {
				return meetingRoomListDto;
			}
			meetingRoomDto.setUserCount(roomMembers.size());
			List<UserInfoDto> userInfoList = new ArrayList<>();
			meetingRoomDto.setUserInfoList(userInfoList);
			meetingRoomListDto.getMeetingRoomList().add(meetingRoomDto);
		}
		simpMessagingTemplate.convertAndSend("/topic/" + workSpaceId + "/meetingRoomList", meetingRoomListDto);
		return meetingRoomListDto;
	}

	public MeetingRoomListDto sendRoomList() {
		Long userId = SecurityUtil.getCurrentUserId();
		String workSpaceId = userService.getUserWorkSpaceId(userId);
		return sendRoomList(workSpaceId);
	}

	private void sendUserList(String workSpaceId, Long roomId) {
		Set<String> userIds = redisRepository.getRoomMembers(workSpaceId, roomId);
		List<UserInfoDto> userInfoList = userService.getUserInfoList(userIds);
		simpMessagingTemplate.convertAndSend("/topic/meetingRoom/" + roomId + "/users", userInfoList);
	}

}
