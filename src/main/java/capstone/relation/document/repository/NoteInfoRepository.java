package capstone.relation.document.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import capstone.relation.document.domain.NoteInfo;

public interface NoteInfoRepository extends MongoRepository<NoteInfo, String>{
}
