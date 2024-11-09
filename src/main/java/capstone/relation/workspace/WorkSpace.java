package capstone.relation.workspace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capstone.relation.document.domain.Folder;
import capstone.relation.meeting.domain.MeetRoom;
import capstone.relation.user.domain.User;
import capstone.relation.workspace.school.domain.School;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
public class WorkSpace {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL)
	private Set<User> users = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private School school;

	@Column(name = "workspace_name")
	private String name;

	@OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL)
	private Set<MeetRoom> meetRooms = new HashSet<>();

	@OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL)
	private List<Folder> folders = new ArrayList<>();

	@Column(nullable = false)
	private boolean deleted;

	public void addUser(User user) {
		this.users.add(user);
		user.setWorkSpace(this);
		user.setInvitedWorkspaceId("");
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public void delete() {
		this.deleted = true;
	}

	public void addMeetRoom(MeetRoom meetRoom) {
		this.meetRooms.add(meetRoom);
		meetRoom.setWorkSpace(this);
	}
}
