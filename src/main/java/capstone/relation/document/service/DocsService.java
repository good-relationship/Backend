package capstone.relation.document.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import capstone.relation.document.domain.FileInfo;
import capstone.relation.document.domain.Folder;
import capstone.relation.document.domain.NoteInfo;
import capstone.relation.document.dto.FileContentDto;
import capstone.relation.document.dto.FileInfoDto;
import capstone.relation.document.dto.FolderInfoDto;
import capstone.relation.document.exception.DocumentErrorCode;
import capstone.relation.document.exception.DocumentException;
import capstone.relation.document.repository.FileInfoRepository;
import capstone.relation.document.repository.NoteInfoRepository;
import capstone.relation.document.repository.FolderRepository;
import capstone.relation.user.domain.User;
import capstone.relation.user.exception.UserErrorCode;
import capstone.relation.user.exception.UserException;
import capstone.relation.user.repository.UserRepository;
import capstone.relation.workspace.WorkSpace;
import capstone.relation.workspace.exception.WorkSpaceErrorCode;
import capstone.relation.workspace.exception.WorkSpaceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocsService {

	private final NoteInfoRepository noteInfoRepository;
	private final FolderRepository folderRepository;
	private final FileInfoRepository fileRepository;
	private final UserRepository userRepository;

	@Transactional
	public FolderInfoDto createFolder(String folderName, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.INVALID_USER));
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null)
			throw new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE);
		Folder folder = Folder.builder()
			.folderName(folderName)
			.workSpace(workSpace)
			.build();
		folderRepository.save(folder);
		return FolderInfoDto.builder()
			.folderId(folder.getId())
			.folderName(folderName)
			.build();
	}

	public List<FolderInfoDto> getAllDocs(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.INVALID_USER));
		WorkSpace workSpace = user.getWorkSpace();
		if (workSpace == null)
			throw new WorkSpaceException(WorkSpaceErrorCode.NO_WORKSPACE);
		List<Folder> folders = workSpace.getFolders();
		List<FolderInfoDto> folderInfoDtos = new ArrayList<>();
		for (Folder folder : folders) {
			FolderInfoDto folderInfoDto = FolderInfoDto.builder()
				.folderId(folder.getId())
				.folderName(folder.getFolderName())
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

	@Transactional
	public FileContentDto createFile(Long userId, String fileName, Long folderId, String content) {
		Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new DocumentException(
			DocumentErrorCode.FOLDER_NOT_EXIST));
		WorkSpace workSpace = folder.getWorkSpace();
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.INVALID_USER));
		if (!workSpace.getId().equals(user.getWorkSpace().getId()))
			throw new DocumentException(DocumentErrorCode.USER_NOT_ACCESS);
		NoteInfo noteInfo = NoteInfo.builder().content(content).build();
		noteInfoRepository.save(noteInfo);
		FileInfo file = FileInfo.builder()
			.fileName(fileName)
			.folder(folder)
			.noteInfoId(noteInfo.getId())
			.build();
		fileRepository.save(file);
		folder.getFileInfos().add(file);
		return FileContentDto.builder()
			.fileId(file.getId())
			.fileName(fileName)
			.content(content)
			.build();
	}

	@Transactional
	public FileContentDto updateFile(Long fileId, String fileName, Long folderId, String content) {
		FileInfo fileInfo = fileRepository.findById(fileId)
			.orElseThrow(() -> new DocumentException(DocumentErrorCode.FILE_NOT_EXIST));
		NoteInfo noteInfo = noteInfoRepository.findById(fileInfo.getNoteInfoId())
			.orElseThrow(() -> new DocumentException(DocumentErrorCode.FILE_NOT_EXIST));
		noteInfo.setContent(content);
		noteInfoRepository.save(noteInfo);
		fileInfo.setFileName(fileName);
		fileRepository.save(fileInfo);
		return FileContentDto.builder()
			.fileId(fileInfo.getId())
			.fileName(fileName)
			.content(content)
			.build();
	}

	public FolderInfoDto getFolder(Long folderId) {
		Folder folder = folderRepository.findById(folderId)
			.orElseThrow(() -> new DocumentException(DocumentErrorCode.FOLDER_NOT_EXIST));
		List<FileInfo> fileInfos = folder.getFileInfos();
		List<FileInfoDto> fileInfoDtos = new ArrayList<>();
		for (FileInfo fileInfo : fileInfos) {
			fileInfoDtos.add(FileInfoDto.builder()
				.fileId(fileInfo.getId())
				.fileName(fileInfo.getFileName())
				.build());
		}
		return FolderInfoDto.builder()
			.folderId(folder.getId())
			.folderName(folder.getFolderName())
			.files(fileInfoDtos)
			.build();
	}

	public FileContentDto getFile(String id) {
		return null;
	}
}