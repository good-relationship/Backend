package capstone.relation.workspace.school.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(SchoolId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)  // 모든 필드를 포함하는 생성자 추가
@Builder
@Getter
@Setter
public class School {

	@Id
	private String campusName; //1캠퍼스 2캠퍼스
	@Id
	private String schoolName; //학교명

	private String schoolType; //대학교,전문대학, 사이버대학 등
	private String link; //url
	private String schoolGubun; //대학(4년제), 전문대학 ..
	private String adres; //주소
	private String region; //지역 경기도
	private String estType; //사립 국립 등
}
