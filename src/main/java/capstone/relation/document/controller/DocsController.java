package capstone.relation.document.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import capstone.relation.document.dto.UpdateFileReqDto;
import capstone.relation.document.service.DocsService;
import capstone.relation.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/docs")
public class DocsController {

	private final DocsService docsService;

	@GetMapping("/all")
	@Operation(summary = "모든 폴더 정보 요청", description = "현재 워크스페이스의 모든 폴더와 파일 정보를 요청합니다.")
	public List<FolderInfoDto> getAllFolder() {
		return docsService.getAllDocs(SecurityUtil.getCurrentUserId());
	}

	@PostMapping("/folder")
	@Operation(summary = "폴더 생성", description = "폴더를 생성합니다.")
	public FolderInfoDto createFolder(@RequestBody FolderCreateDto folderCreateDto) {
		return docsService.createFolder(folderCreateDto.getFolderName(), SecurityUtil.getCurrentUserId());
	}

	@GetMapping("/folder/{id}")
	@Operation(summary = "폴더 정보 요청", description = "폴더의 정보를 요청합니다.")
	public FolderInfoDto getFolder(@PathVariable Long id) {
		return docsService.getFolder(id);
	}

	@PutMapping("/folder/{id}")
	@Operation(summary = "폴더명 수정", description = "폴더를 수정합니다.")
	public FolderInfoDto updateFolder(@PathVariable Long id, @RequestBody FolderCreateDto folderCreateDto) {
		return docsService.updateFolder(id, folderCreateDto.getFolderName());
	}

	@DeleteMapping("/folder/{id}")
	@Operation(summary = "폴더 삭제", description = "폴더를 삭제합니다.")
	public void deleteFolder(@PathVariable Long id) {
		docsService.deleteFolder(SecurityUtil.getCurrentUserId(), id);
	}



	@PostMapping("/file")
	@Operation(summary = "파일 생성", description = "파일을 생성합니다.")
	public FileContentDto createFile(@RequestBody CreateFileReqDto createFileReqDto) {
		return docsService.createFile(SecurityUtil.getCurrentUserId(),
			createFileReqDto.getFileName(), createFileReqDto.getFolderId());
	}

	@PatchMapping("/file/{id}")
	@Operation(summary = "파일 수정", description = "파일을 수정합니다.")
	public FileContentDto updateFile(@PathVariable Long id, @RequestBody UpdateFileReqDto updateFileReqDto) {
		return docsService.updateFile(SecurityUtil.getCurrentUserId(), id, updateFileReqDto.getContent());
	}

	@DeleteMapping("/file/{id}")
	@Operation(summary = "파일 삭제", description = "파일을 삭제합니다.")
	public void deleteFile(@PathVariable Long id) {
		docsService.deleteFile(SecurityUtil.getCurrentUserId(), id);
	}


	@GetMapping("/file/{id}")
	@Operation(summary = "파일 정보(파일의 내용)요청", description = "파일의 정보를 요청합니다.")
	public FileContentDto getFile(@PathVariable Long id) {
		return docsService.getFile(id);
	}


}
