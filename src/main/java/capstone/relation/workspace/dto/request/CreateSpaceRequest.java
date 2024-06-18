package capstone.relation.workspace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSpaceRequest {
	@NotBlank(message = "Workspace name is required.")
	@Size(min = 1, max = 50, message = "Workspace name must be between 1 and 50 characters.")
	@Schema(description = "워크스페이스 이름", example = "캡스톤 디자인")
	private String workspaceName;

	@NotBlank(message = "School name is required.")
	@Size(min = 1, max = 50, message = "School name must be between 1 and 50 characters.")
	@Schema(description = "학교 이름", example = "서울대학교")
	private String schoolName;

}
