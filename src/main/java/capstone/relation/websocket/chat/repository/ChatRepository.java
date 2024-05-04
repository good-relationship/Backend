package capstone.relation.websocket.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import capstone.relation.websocket.chat.domain.Chat;
import capstone.relation.workspace.WorkSpace;

@Repository

public interface ChatRepository extends JpaRepository<Chat, Long> {

	List<Chat> findTop10ByWorkSpaceAndIdLessThanOrderByIdDesc(WorkSpace workSpace, Long lastMsgId);

	List<Chat> findTop10ByWorkSpaceOrderByIdDesc(WorkSpace workSpace);
}
