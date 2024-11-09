package capstone.relation.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import capstone.relation.document.domain.FileInfo;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
}
