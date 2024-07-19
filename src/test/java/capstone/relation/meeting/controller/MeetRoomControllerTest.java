package capstone.relation.meeting.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.dto.response.JoinResponseDto;
import capstone.relation.meeting.service.MeetRoomService;
import capstone.relation.security.WithMockCustomUser;
import capstone.relation.user.dto.UserInfoDto;

@ExtendWith(SpringExtension.class)
@DisplayName("회의방 컨트롤러 테스트")
class MeetRoomControllerTest {

	@InjectMocks
	private MeetRoomController meetRoomController;

	@Mock
	private MeetRoomService meetRoomService;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(meetRoomController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	@DisplayName("방 생성")
	void testCreateRoom() throws Exception {
		CreateRoomDto createRoomDto = new CreateRoomDto("회의방");
		List<UserInfoDto> userInfoList = new ArrayList<>();
		UserInfoDto userInfoDto = new UserInfoDto(1L, "이름", "이메일", "사진");
		userInfoList.add(userInfoDto);
		JoinResponseDto joinResponseDto = new JoinResponseDto(1L, "회의방", userInfoList, 1L);

		when(meetRoomService.createAndJoinRoom(any(CreateRoomDto.class)))
			.thenReturn(joinResponseDto);

		mockMvc.perform(post("/meet/room/create")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(createRoomDto)))  // CreateRoomDto 객체를 JSON 문자열로 변환
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roomId").value(joinResponseDto.getRoomId()))
			.andExpect(jsonPath("$.roomName").value(joinResponseDto.getRoomName()))
			.andExpect(jsonPath("$.userInfoList[0].userId").value(userInfoDto.getUserId()))
			.andExpect(jsonPath("$.userInfoList[0].userName").value(userInfoDto.getUserName()))
			.andExpect(jsonPath("$.userInfoList[0].email").value(userInfoDto.getEmail()))
			.andExpect(jsonPath("$.userInfoList[0].userImage").value(userInfoDto.getUserImage()))
			.andExpect(jsonPath("$.userCount").value(joinResponseDto.getUserCount()));
	}

	@Test
	@DisplayName("방 나가기 테스트")
	@WithMockCustomUser
	void testLeaveRoom() throws Exception {
		mockMvc.perform(post("/meet/room/leave"))
			.andExpect(status().isOk());
	}
}
