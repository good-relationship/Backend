package capstone.relation.document.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import capstone.relation.document.dto.CreateFileReqDto;
import capstone.relation.document.dto.FileContentDto;
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
	public List<FolderInfoDto> getAllFolder() {
		return docsService.getAllDocs(SecurityUtil.getCurrentUserId());
	}

	@PostMapping("/folder")
	public FolderInfoDto createFolder(@RequestBody FolderCreateDto folderCreateDto) {
		return docsService.createFolder(folderCreateDto.getFolderName(), SecurityUtil.getCurrentUserId());
	}

	@GetMapping("/folder/{id}")
	public FolderInfoDto getFolder(@PathVariable Long id) {
		return docsService.getFolder(id);
	}

	@PostMapping("/file")
	public FileContentDto createFile(@RequestBody CreateFileReqDto createFileReqDto) {
		return docsService.createFile(SecurityUtil.getCurrentUserId(),
			createFileReqDto.getFileName(), createFileReqDto.getFolderId(), createFileReqDto.getContent());
	}

	@PutMapping("/file/{id}")
	public FileContentDto updateFile(@PathVariable Long id, @RequestBody CreateFileReqDto createFileReqDto) {
		return docsService.updateFile(id, createFileReqDto.getFileName(),
			createFileReqDto.getFolderId(), createFileReqDto.getContent());
	}

	@GetMapping("/file/{id}")
	public FileContentDto getFile(@PathVariable Long id) {
		return docsService.getFile(id);
	}


}
