package capstone.relation.meeting.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import capstone.relation.meeting.domain.MeetRoom;

@Repository
public interface MeetRoomRepository extends JpaRepository<MeetRoom, Long> {
	Set<MeetRoom> findAllByWorkSpaceId(String workSpaceId);
}
