package capstone.relation.workspace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSpaceRequest {
	@NotBlank(message = "Workspace name is required.")
	@Size(min = 1, max = 50, message = "Workspace name must be between 1 and 50 characters.")
	private String workspaceName;

	@NotBlank(message = "School name is required.")
	@Size(min = 1, max = 50, message = "School name must be between 1 and 50 characters.")
	private String schoolName;

}
