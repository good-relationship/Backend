package capstone.relation.workspace.school.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.domain.SchoolId;

@Repository
public interface SchoolRepository extends JpaRepository<School, SchoolId> {
	List<School> findBySchoolNameContaining(String name);
}
