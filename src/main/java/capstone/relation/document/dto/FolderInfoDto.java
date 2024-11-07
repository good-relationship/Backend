package capstone.relation.document.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderInfoDto {
	private Long folderId;
	private String folderName;
	private List<FileInfoDto> files;
}
