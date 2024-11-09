package capstone.relation.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "폴더 생성 요청")
public class FolderCreateDto {
	@Schema(example = "빈폴더 이름")
	private String folderName;
}
