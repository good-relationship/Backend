package capstone.relation.workspace.school.domain;

import java.io.Serializable;
import java.util.Objects;

public class SchoolId implements Serializable {
	private String campusName;
	private String schoolName;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		SchoolId schoolId = (SchoolId)obj;
		return Objects.equals(campusName, schoolId.campusName)
			&& Objects.equals(schoolName, schoolId.schoolName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(campusName, schoolName);
	}
}
