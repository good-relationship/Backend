package capstone.relation.document.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.document.domain.NoteInfo;
import capstone.relation.document.dto.CreateFileReqDto;
import capstone.relation.document.dto.FolderCreateDto;
import capstone.relation.document.dto.FolderInfoDto;
import capstone.relation.document.service.DocsService;
import capstone.relation.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/docs")
public class DocsController {

	private final DocsService docsService;

	@GetMapping("/all")
	public List<FolderInfoDto> getAllFolder(){
		return docsService.getAllDocs(SecurityUtil.getCurrentUserId());
	}

	@GetMapping("/folder/{id}")
	public FolderInfoDto getFolder(@PathVariable Long id) {
		return docsService.getFolder(id);
	}

	@GetMapping("/file/{id}")
	public NoteInfo getFile(@PathVariable String id) {
		return docsService.getFile(id);
	}

	@PostMapping("/folder")
	public FolderInfoDto createFolder(@RequestBody FolderCreateDto folderCreateDto) {
		return docsService.createFolder(folderCreateDto.getFolderName());
	}

	@PostMapping("/file")
	public void createFile(@RequestBody CreateFileReqDto createFileReqDto) {
		docsService.createFile(createFileReqDto);
	}
}
