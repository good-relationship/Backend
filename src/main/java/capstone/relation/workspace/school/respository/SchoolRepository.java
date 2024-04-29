package capstone.relation.workspace.school.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.domain.SchoolId;

@Repository
public interface SchoolRepository extends JpaRepository<School, SchoolId> {
	List<School> findBySchoolNameContaining(String name);

	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM School s WHERE s.schoolName = :name")
	boolean findByName(String name);
}
