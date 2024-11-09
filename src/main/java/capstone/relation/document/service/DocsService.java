package capstone.relation.document.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import capstone.relation.document.domain.FileInfo;
import capstone.relation.document.domain.Folder;
import capstone.relation.document.domain.NoteInfo;
import capstone.relation.document.dto.CreateFileReqDto;
import capstone.relation.document.dto.FileInfoDto;
import capstone.relation.document.dto.FolderInfoDto;
import capstone.relation.document.repository.DocsRepository;
import capstone.relation.document.repository.FolderRepository;
import capstone.relation.user.domain.User;
import capstone.relation.user.exception.UserErrorCode;
import capstone.relation.user.exception.UserException;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocsService {

	private final DocsRepository docsRepository;
	private final FolderRepository folderRepository;
	private final UserRepository userRepository;

	public FolderInfoDto createFolder(String folderName) {
		return null;
	}

	public List<FolderInfoDto> getAllDocs(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.INVALID_USER));
		WorkSpace workSpace = user.getWorkSpace();
		List<Folder> folders = workSpace.getFolders();
		List<FolderInfoDto> folderInfoDtos = new ArrayList<>();
		for (Folder folder : folders) {
			FolderInfoDto folderInfoDto = FolderInfoDto.builder()
				.folderId(folder.getId())
				.folderName(folder.getCategoryName())
				.build();
			List<FileInfo> fileInfos = folder.getFileInfos();
			for (FileInfo fileInfo : fileInfos) {
				FileInfoDto fileInfoDto = FileInfoDto.builder()
					.fileId(fileInfo.getId())
					.fileName(fileInfo.getFileName())
					.build();
				folderInfoDto.getFiles().add(fileInfoDto);
			}
			folderInfoDtos.add(folderInfoDto);
		}
		return folderInfoDtos;
	}

	public FileInfoDto createFile(CreateFileReqDto createFileReqDto) {
		Folder folder = folderRepository.findById(createFileReqDto.getFolderId()).orElseThrow(() -> new IllegalArgumentException("폴더가 존재하지 않습니다."));
		// folder.getDocuments().add(createFileReqDto.getFileName());
		NoteInfo noteInfo = NoteInfo.builder()
			.name(createFileReqDto.getFileName())
			.age(0L)
			.build();
		FileInfo fileInfo = FileInfo.builder().build();
		return null;
	}

	public FolderInfoDto getFolder(Long folderId) {
		return null;
	}

	public NoteInfo getFile(String id) {
		return null;
	}
}
