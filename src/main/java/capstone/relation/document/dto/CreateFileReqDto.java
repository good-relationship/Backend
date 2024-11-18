package capstone.relation.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateFileReqDto {
	@Schema(description = "파일 이름", example = "test")
	private String fileName;
	@Schema(description = "폴더 아이디", example = "1")
	private Long folderId;
	@Schema(description = "파일 내용", example = "test")
	private String content;
}
