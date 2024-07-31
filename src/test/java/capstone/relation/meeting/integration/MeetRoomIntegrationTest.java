package capstone.relation.meeting.integration;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.meeting.dto.request.CreateRoomDto;
import capstone.relation.meeting.repository.MeetRoomRepository;
import capstone.relation.meeting.repository.RedisRepository;
import capstone.relation.security.WithMockCustomUser;
import capstone.relation.user.domain.Role;
import capstone.relation.user.domain.User;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.respository.SchoolRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("회의방 통합 테스트")
public class MeetRoomIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	UserRepository userRepository;

	@Autowired
	WorkSpaceRepository workSpaceRepository;

	@Autowired
	SchoolRepository schoolRepository;

	@Autowired
	MeetRoomRepository meetRoomRepository;

	@MockBean
	private RedisRepository redisRepository;

	private WorkSpace testWorkSpace;
	private School testSchool;

	@BeforeEach
	void setup() {
		redisRepository.deleteAll(); // Redis 저장소 초기화
		testSchool = new School("서울캠", "과기대", "4년제", "링크", "학교소개", "학교위치", "학교지역", "학교종류", new HashSet<>());
		schoolRepository.save(testSchool);

		testWorkSpace = new WorkSpace();
		testWorkSpace.setName("testWorkSpace");
		testWorkSpace.setSchool(testSchool);
		workSpaceRepository.save(testWorkSpace);

		addTestUser("testUser1");
		addTestUser("testUser2");
		addTestUser("testUser3");

		// RedisRepository 모킹
		when(redisRepository.isUserInRoom(any())).thenReturn(false);
		when(redisRepository.getUserRoomId(1L)).thenReturn(null);
		when(redisRepository.getRoomMemberIds(testWorkSpace.getId(), 1L)).thenReturn(new HashSet<>(Set.of("1")));
		when(redisRepository.getRoomMemberIds(testWorkSpace.getId(), 2L)).thenReturn(new HashSet<>(Set.of("1")));
	}

	private void addTestUser(String username) {
		User user = User.builder().userName(username)
			.email(username + "@naver.com")
			.profileImage(
				"https://lh3.googleusercontent.com/ogw/AF2bZyhqowurXq6imx61oPHn5G_c6OIEnucOyJanitxYGFUI498=s32-c-mo")
			.role(Role.USER)
			.provider("naver")
			.build();
		workSpaceRepository.save(testWorkSpace);
		user.setWorkSpace(testWorkSpace);
		userRepository.save(user);
	}

	@Test
	@DisplayName("통합 테스트 : 새로운 방 생성을 할 수 있다.")
	@WithMockCustomUser
	@DirtiesContext
	void createRoom() throws Exception {
		// given
		CreateRoomDto createRoomDto = new CreateRoomDto("New Room");
		String requestJson = objectMapper.writeValueAsString(createRoomDto);

		// when & then
		mockMvc.perform(post("/meet/room/create")
				.contentType("application/json")
				.content(requestJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roomId").isNumber())
			.andExpect(jsonPath("$.roomName").value("New Room"))
			.andExpect(jsonPath("$.userInfoList").isArray())
			.andExpect(jsonPath("$.userCount").value(1));
	}

	@Test
	@DisplayName("통합 테스트 : 존재하는 회의방에 참여할 수 있다.")
	@WithMockCustomUser
	@DirtiesContext
	void joinRoom() throws Exception {
		// given
		MeetRoom meetRoom = MeetRoom.builder()
			.roomName("채팅방")
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom);
		// when & then
		mockMvc.perform(post("/meet/room/join/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roomId").isNumber())
			.andExpect(jsonPath("$.roomName").value("채팅방"))
			.andExpect(jsonPath("$.userInfoList").isArray())
			.andExpect(jsonPath("$.userCount").value(1));
	}

	@Test
	@DisplayName("통합 테스트 : 존재하지 않는 회의방에 참여할 수 없다.")
	@DirtiesContext
	@WithMockCustomUser
	void joinRoomFail() throws Exception {
		// when & then
		mockMvc.perform(post("/meet/room/join/1"))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("통합 테스트 : 회의방에서 나갈 수 있다.")
	@WithMockCustomUser(id = 2L)
	@DirtiesContext
	void leaveRoom() throws Exception {
		// given
		MeetRoom meetRoom = MeetRoom.builder()
			.roomName("채팅방1")
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom);
		MeetRoom meetRoom2 = MeetRoom.builder()
			.roomName("채팅방2")
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom2);
		// join 시에 필요한 모킹 설정
		when(redisRepository.getUserRoomId(anyLong())).thenReturn(null);
		when(redisRepository.getRoomMemberIds(any(), anyLong())).thenReturn(new HashSet<>(Set.of("2")));
		// when & then
		mockMvc.perform(post("/meet/room/join/2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roomId").isNumber())
			.andExpect(jsonPath("$.roomName").value("채팅방2"))
			.andExpect(jsonPath("$.userInfoList").isArray())
			.andExpect(jsonPath("$.userCount").value(1));
		// leave 시에 필요한 모킹 설정
		when(redisRepository.getUserRoomId(anyLong())).thenReturn(meetRoom2.getRoomId().toString());

		mockMvc.perform(post("/meet/room/leave"))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("통합 테스트 : 회의방 목록을 요청할 수 있다.")
	@WithMockCustomUser
	@DirtiesContext
	void requestRoomList() throws Exception {
		// given
		MeetRoom meetRoom = MeetRoom.builder()
			.roomName("채팅방1")
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom);
		MeetRoom meetRoom2 = MeetRoom.builder()
			.roomName("채팅방2")
			.deleted(false)
			.build();
		meetRoomRepository.save(meetRoom2);

		testWorkSpace.addMeetRoom(meetRoom);
		testWorkSpace.addMeetRoom(meetRoom2);
		workSpaceRepository.save(testWorkSpace);

		// join 시에 필요한 모킹 설정
		when(redisRepository.getUserRoomId(anyLong())).thenReturn(null);
		when(redisRepository.getRoomMemberIds(any(), anyLong())).thenReturn(new HashSet<>(Set.of("1")));
		// when & then
		mockMvc.perform(get("/meet/room/list"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.meetingRoomList").isArray())
			.andExpect(jsonPath("$.meetingRoomList[0].roomId").isNumber())
			.andExpect(jsonPath("$.meetingRoomList[0].roomName").value("채팅방1"))
			.andExpect(jsonPath("$.meetingRoomList[0].userCount").value(1))
			.andExpect(jsonPath("$.meetingRoomList[0].userInfoList").isArray())
			.andExpect(jsonPath("$.meetingRoomList[1].roomId").isNumber())
			.andExpect(jsonPath("$.meetingRoomList[1].roomName").value("채팅방2"))
			.andExpect(jsonPath("$.meetingRoomList[1].userCount").value(1))
			.andExpect(jsonPath("$.meetingRoomList[1].userInfoList").isArray());
	}
}
