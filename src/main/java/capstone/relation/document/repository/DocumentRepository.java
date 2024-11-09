package capstone.relation.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import capstone.relation.document.domain.FileInfo;

public interface DocumentRepository extends JpaRepository<FileInfo, Long> {
}
