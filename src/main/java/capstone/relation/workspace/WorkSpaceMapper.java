package capstone.relation.workspace;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import capstone.relation.workspace.dto.response.WorkspaceInfo;

@Mapper
public interface WorkSpaceMapper {
	WorkSpaceMapper INSTANCE = Mappers.getMapper(WorkSpaceMapper.class);

	@Mapping(target = "workspaceId", source = "id")
	@Mapping(target = "workspaceName", source = "name")
	WorkspaceInfo toDto(WorkSpace entity);
}
