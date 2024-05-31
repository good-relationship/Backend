package capstone.relation.meeting.domain;

import org.hibernate.annotations.SQLRestriction;

import capstone.relation.workspace.WorkSpace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@SQLRestriction(value = "deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MeetRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roomId;

	@Column(nullable = false)
	private String roomName;
	@Column(nullable = false)
	private boolean deleted = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY)
	private WorkSpace workSpace;

	@Builder
	public MeetRoom(Long roomId, String roomName, WorkSpace workSpace, boolean deleted) {
		this.roomId = roomId;
		this.roomName = roomName;
		this.deleted = deleted;
	}

}
