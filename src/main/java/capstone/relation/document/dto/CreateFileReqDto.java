package capstone.relation.document.dto;

import lombok.Data;

@Data
public class CreateFileReqDto {
	private String fileName;
	private Long folderId;
}
