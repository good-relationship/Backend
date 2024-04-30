package capstone.relation.websocket.chat.domain;

import java.time.LocalDateTime;

import capstone.relation.user.domain.User;
import capstone.relation.workspace.WorkSpace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private LocalDateTime timestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	WorkSpace workSpace;

	public Chat(String content, LocalDateTime timestamp) {
		this.content = content;
		this.timestamp = timestamp;
	}

	public Chat(User user, WorkSpace workSpace, String content, LocalDateTime timestamp) {
		this.sender = user;
		this.workSpace = workSpace;
		this.content = content;
		this.timestamp = timestamp;
	}
}