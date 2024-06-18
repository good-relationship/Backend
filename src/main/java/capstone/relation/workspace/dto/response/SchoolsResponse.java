package capstone.relation.workspace.dto.response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capstone.relation.workspace.school.domain.School;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SchoolsResponse {
	@Schema(description = "학교 수", example = "1")
	private int count;
	@Schema(description = "학교 목록", example = "[\"서울과학기술대학교\"]")
	private String[] schools;

	public void setDummy() {
		this.count = 1;
		this.schools = new String[] {"서울과학기술대학교"};
	}

	public void setBySchools(List<School> schools) {
		Set<String> uniqueSchoolNames = new HashSet<>();
		for (School school : schools) {
			uniqueSchoolNames.add(school.getSchoolName());
		}
		this.count = uniqueSchoolNames.size();
		this.schools = uniqueSchoolNames.toArray(new String[0]);
	}
}
