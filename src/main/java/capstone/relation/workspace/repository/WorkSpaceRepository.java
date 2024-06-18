package capstone.relation.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import capstone.relation.workspace.WorkSpace;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace, String> {
}
