package capstone.relation.workspace.school.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolInfoRequest {
	private String apiKey;
	private String svcType = "api";
	private String svcCode = "SCHOOL";
	private String contentType = "json";
	private String gubun = "univ_list";
	private String perPage = "1000"; // Optional
	private String searchSchulNm; // Optional

	public SchoolInfoRequest(String apiKey) {
		this.apiKey = apiKey;
	}
	// getters and setters
}