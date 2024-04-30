package capstone.relation.websocket.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import capstone.relation.websocket.chat.domain.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	
}
