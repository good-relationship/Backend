package capstone.relation.workspace.dto.response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import capstone.relation.workspace.school.domain.School;
import lombok.Data;

@Data
public class SchoolsResponse {
	private int count;
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
