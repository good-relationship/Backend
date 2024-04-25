package capstone.relation.workspace.dto.response;

import lombok.Data;

@Data
public class SchoolsResponse {
	private int count;
	private String[] schools;

	public void setDummy() {
		this.count = 1;
		this.schools = new String[] {"서울과학기술대학교"};
	}
}
