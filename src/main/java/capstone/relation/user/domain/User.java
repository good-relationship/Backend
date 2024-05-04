package capstone.relation.user.domain;

import org.apache.commons.validator.routines.EmailValidator;

import capstone.relation.workspace.WorkSpace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "users")
public class User {
	//TODO: 프로필 이미지 디폴트값 설정 필요
	@Builder
	public User(Long id, String profileImage, String userName, String email, String provider, Role role) {
		if (!EmailValidator.getInstance().isValid(email)) {
			throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
		}
		this.profileImage = profileImage;
		this.userName = userName;
		this.provider = provider;
		this.email = email;
		this.role = role;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "profile_image")
	private String profileImage; //url
	@Column(name = "user_name", nullable = false)
	private String userName;
	@Column(name = "email", nullable = false)
	private String email;
	@Column(name = "provider", nullable = false)
	private String provider;
	@Column(name = "invited_space_id", nullable = true)
	private String invitedWorkspaceId;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	private WorkSpace workSpace;

	public void setWorkSpace(WorkSpace workSpace) {
		this.workSpace = workSpace;
	}

	public void setInvitedWorkspaceId(String invitedWorkspaceId) {
		if (invitedWorkspaceId == "") {
			this.invitedWorkspaceId = null;
		} else {
			this.invitedWorkspaceId = invitedWorkspaceId;
		}
	}
}
