package capstone.relation.workspace.school.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.domain.SchoolId;

@Repository
public interface SchoolRepository extends JpaRepository<School, SchoolId> {
	List<School> findBySchoolNameContaining(String name);

	@Query("select s from School s where s.schoolName = :name order by s.campusName limit 1")
	Optional<School> findByName(String name);
}
