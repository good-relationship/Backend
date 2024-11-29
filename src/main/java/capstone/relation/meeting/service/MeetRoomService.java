package capstone.relation.meeting.service;

import java.util.List;
import java.util.Set;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.dto.response.JoinResponseDto;
import capstone.relation.meeting.dto.response.MeetingRoomDto;
import capstone.relation.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.meeting.exception.MeetingErrorCode;
import capstone.relation.meeting.exception.MeetingException;
import capstone.relation.meeting.repository.MeetRoomRepository;
import capstone.relation.meeting.repository.RedisRepository;
import capstone.relation.user.UserService;
import capstone.relation.user.domain.User;
import capstone.relation.user.dto.RoomInfoDto;
import capstone.relation.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MeetRoomService {
	private final UserService userService;
	private final RedisRepository redisRepository;
	private final MeetRoomRepository meetRoomRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;

	/**
	 * 미팅룸을 생성하고 참여합니다.
	 * @param createRoomDto 생성할 미팅룸 정보 DTO
	 * @return 참여 응답 DTO
	 */
	@Transactional(readOnly = false)
	public JoinResponseDto createAndJoinRoom(Long userId, CreateRoomDto createRoomDto) {
		String roomName = createRoomDto.getRoomName();
		if (roomName == null || roomName.isEmpty())
			throw new MeetingException(MeetingErrorCode.MEETING_NAME_NOT_EXIST);
		String workSpaceId = userService.getUserWorkSpaceId(userId);
		// 미팅룸을 생성, 참여, 미팅룸 목록을 전송합니다.
		Long roomId = createRoom(userId, roomName);
		JoinResponseDto joinResponseDto = joinWorkspaceRoom(workSpaceId, userId, roomId);
		sendRoomList(workSpaceId);
		return joinResponseDto;
	}

	/**
	 * 미팅룸을 생성합니다.
	 * @param userId 사용자 ID
	 * @param roomName 미팅룸 이름
	 * @return 생성된 미팅룸 ID
	 */
	private Long createRoom(Long userId, String roomName) {
		if (redisRepository.isUserInRoom(userId))
			throw new MeetingException(MeetingErrorCode.MEETING_ALREADY_JOINED);
		User user = userService.getUserEntity();
		MeetRoom meetRoom = MeetRoom.builder()
			.roomName(roomName)
			.deleted(false)
			.workSpace(user.getWorkSpace())
			.build();
		meetRoomRepository.save(meetRoom);
		return meetRoom.getRoomId();
	}

	/**
	 * 미팅룸 정보를 조회합니다.
	 * @param workspaceId 워크스페이스 ID
	 * @param userId 사용자 ID
	 * @return 미팅룸 정보 DTO
	 */
	public RoomInfoDto getRoomInfo(String workspaceId, Long userId) {
		if (!redisRepository.isUserInRoom(userId))
			throw new MeetingException(MeetingErrorCode.USER_NOT_MEETING_MEMBER);

		Long roomId = Long.parseLong(redisRepository.getUserRoomId(userId));
		Set<String> userIds = redisRepository.getRoomMemberIds(workspaceId, roomId);
		List<UserInfoDto> userInfoList = userService.getUserInfoList(userIds);
		return new RoomInfoDto(true, roomId, getRoomName(roomId), userInfoList);
	}

	/**
	 * 미팅룸 이름을 조회합니다.
	 * @param roomId 미팅룸 ID
	 * @return 미팅룸 이름
	 */
	private String getRoomName(Long roomId) {
		return meetRoomRepository.findById(roomId)
			.orElseThrow(() -> new MeetingException(MeetingErrorCode.INVALID_MEETING))
			.getRoomName();
	}

	/**
	 * 미팅룸에 참여합니다. 컨트롤러 연결 메서드
	 * @param roomId 가입하고자 하는 미팅룸 ID
	 * @return 참여 응답 DTO
	 */
	public JoinResponseDto joinRoom(Long userId, Long roomId) {

		String workspaceId = userService.getUserWorkSpaceId(userId);
		String meetRoomId = redisRepository.getUserRoomId(userId);
		if (meetRoomId != null)
			throw new MeetingException(MeetingErrorCode.MEETING_ALREADY_JOINED);

		JoinResponseDto joinResponseDto = joinWorkspaceRoom(workspaceId, userId, roomId);
		sendRoomList(workspaceId);
		return joinResponseDto;
	}

	/**
	 * 미팅룸에 참여합니다.
	 * 웹소켓으로 변경된 사용자 목록을 전송합니다.
	 * @param workSpaceId 워크스페이스 ID
	 * @param userId 사용자 ID
	 * @param roomId 미팅룸 ID
	 * @return 참여 응답 DTO
	 */
	private JoinResponseDto joinWorkspaceRoom(String workSpaceId, Long userId, Long roomId) {
		MeetRoom meetRoom = meetRoomRepository.findById(roomId)
			.orElseThrow(() -> new MeetingException(MeetingErrorCode.INVALID_MEETING));
		redisRepository.addUserToRoom(workSpaceId, roomId, userId.toString());
		Set<String> userIds = redisRepository.getRoomMemberIds(workSpaceId, roomId);
		List<UserInfoDto> userInfoList = userService.getUserInfoList(userIds);
		sendUserList(workSpaceId, roomId);
		return new JoinResponseDto(roomId, meetRoom.getRoomName(), userInfoList, (long)userIds.size());
	}

	/**
	 * 미팅룸에서 나갑니다.
	 * 웹소켓으로 변경된 사용자 목록을 전송합니다.
	 * @param userId 사용자 ID
	 */
	@Transactional(readOnly = false)
	public void leaveRoom(Long userId) {
		String workspaceId = userService.getUserWorkSpaceId(userId);
		String meetRoomId = redisRepository.getUserRoomId(userId);
		if (meetRoomId == null)
			return;
		redisRepository.removeUserFromRoom(workspaceId, Long.parseLong(meetRoomId), userId.toString());
		sendUserList(workspaceId, Long.parseLong(meetRoomId));
		sendRoomList(workspaceId);
	}

	/**
	 * 미팅룸 목록을 웹소켓을 통해 전송합니다.
	 * 이벤트 : /topic/{workSpaceId}/meetingRoomList
	 * 전송 데이터 : 워크스페이스에 있는 미팅룸 목록
	 * @param workSpaceId 참여중인 워크스페이스 ID
	 * @return 미팅룸 목록
	 */
	public MeetingRoomListDto sendRoomList(String workSpaceId) {
		// 미팅룸 정보를 조회합니다.
		Set<MeetRoom> meetRooms = meetRoomRepository.findAllByWorkSpaceId(workSpaceId);
		MeetingRoomListDto meetingRoomListDto = new MeetingRoomListDto();
		// 미팅룸 정보를 DTO로 변환합니다.
		for (MeetRoom meetRoom : meetRooms) {
			MeetingRoomDto meetingRoomDto = new MeetingRoomDto();
			meetingRoomDto.setRoomId(meetRoom.getRoomId());
			meetingRoomDto.setRoomName(meetRoom.getRoomName());

			// 미팅룸에 참여한 사용자 수를 조회합니다.
			Set<String> roomMembers = redisRepository.getRoomMemberIds(workSpaceId, meetRoom.getRoomId());
			meetingRoomDto.setUserCount(roomMembers.size());

			// 사용자 정보 목록을 저장합니다.
			List<UserInfoDto> userInfoList = userService.getUserInfoList(roomMembers);
			meetingRoomDto.setUserInfoList(userInfoList);

			meetingRoomListDto.getMeetingRoomList().add(meetingRoomDto);
		}

		// 미팅룸 정보를 전송합니다.(웹소켓)
		simpMessagingTemplate.convertAndSend("/topic/" + workSpaceId + "/meetingRoomList", meetingRoomListDto);
		return meetingRoomListDto;
	}

	/**
	 * 미팅룸 목록을 웹소켓을 통해서 전송합니다.(컨트롤러 연결 메서드)
	 * 이벤트 : /topic/meetingRoom/{roomId}/users
	 * 전송 데이터 : 사용자 정보 리스트
	 * @return 미팅룸 목록
	 */
	public MeetingRoomListDto sendRoomList(Long userId) {

		String workSpaceId = userService.getUserWorkSpaceId(userId);
		return sendRoomList(workSpaceId);
	}

	/**
	 * 미팅룸에 참여한 사용자 목록을 웹소켓을 통해서 전송합니다.
	 * 이벤트 : /topic/meetingRoom/{roomId}/users
	 * 전송 데이터 : 사용자 정보 리스트
	 * @param workSpaceId 워크스페이스 ID
	 * @param roomId 미팅룸 ID
	 */
	private void sendUserList(String workSpaceId, Long roomId) {
		Set<String> userIds = redisRepository.getRoomMemberIds(workSpaceId, roomId);
		List<UserInfoDto> userInfoList = userService.getUserInfoList(userIds);
		simpMessagingTemplate.convertAndSend("/topic/meetingRoom/" + roomId + "/users", userInfoList);
	}
}
