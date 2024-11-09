package capstone.relation.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import capstone.relation.document.domain.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
