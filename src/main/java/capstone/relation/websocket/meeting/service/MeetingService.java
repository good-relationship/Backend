package capstone.relation.websocket.meeting.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import capstone.relation.user.dto.UserInfoDto;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.websocket.meeting.dto.response.JoinResponseDto;
import capstone.relation.websocket.meeting.dto.response.MeetingRoomDto;
import capstone.relation.websocket.meeting.dto.response.MeetingRoomListDto;
import capstone.relation.websocket.meeting.repository.MeetingRoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingService {
	private final MeetingRoomRepository meetingRoomRepository;
	private final UserRepository userRepository;

	public JoinResponseDto createRoom(Map<String, Object> sessionAttributes, String roomName) {
		String workSpaceId = (String)sessionAttributes.get("workSpaceId");
		String roomId = meetingRoomRepository.createRoom(workSpaceId, roomName);
		String userId = sessionAttributes.get("userId").toString();
		Set<String> userIds = meetingRoomRepository.enterRoom(roomId, userId);
		Set<UserInfoDto> userInfoList = new HashSet<>();
		for (String id : userIds) {
			UserInfoDto userInfoDto = new UserInfoDto();
			userRepository.findById(Long.parseLong(id)).ifPresent(userInfoDto::setByUserEntity);
			userInfoList.add(userInfoDto);
		}
		return new JoinResponseDto(roomId, roomName, userInfoList, (long)userIds.size());
	}

	public MeetingRoomListDto getRoomList(String workSpaceId) {
		Set<String> roomList = meetingRoomRepository.getRoomList(workSpaceId);
		MeetingRoomListDto meetingRoomListDto = new MeetingRoomListDto();
		for (String roomId : roomList) {
			MeetingRoomDto meetingRoomDto = new MeetingRoomDto();
			meetingRoomDto.setRoomId(roomId);
			meetingRoomDto.setRoomName(meetingRoomRepository.getRoomName(roomId));
			meetingRoomDto.setUserCount(meetingRoomRepository.getRoomUserCount(roomId));
			meetingRoomListDto.getMeetingRoomList().add(meetingRoomDto);
		}
		return meetingRoomListDto;
	}

	public String getRoomName(String roomId) {
		return meetingRoomRepository.getRoomName(roomId);
	}
}
