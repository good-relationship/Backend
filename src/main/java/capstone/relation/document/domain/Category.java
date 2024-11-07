package capstone.relation.document.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import capstone.relation.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;

@Entity
@Getter
@SQLDelete(sql = "UPDATE shop SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Category extends BaseEntity {
	@Id
	private Long id;

	@Column(name = "category_name")
	private String categoryName;

	private boolean deleted = Boolean.FALSE;


	@OneToMany(mappedBy = "category")
	private List<Document> documents = new ArrayList<>();

}
