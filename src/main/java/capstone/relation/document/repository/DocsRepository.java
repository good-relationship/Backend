package capstone.relation.document.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import capstone.relation.document.domain.NoteInfo;

public interface DocsRepository extends MongoRepository<NoteInfo, String>{
}
