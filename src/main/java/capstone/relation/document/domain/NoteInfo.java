package capstone.relation.document.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collation = "GoodRelDocs") //실제 몽고 DB 컬렉션 명
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoteInfo {
	@Id
	private String id;
	private String name;
	private Long age;

}
