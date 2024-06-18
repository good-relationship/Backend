package capstone.relation.workspace.school;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.dto.response.SchoolInfo;

@Mapper
public interface SchoolMapper {
	SchoolMapper INSTANCE = Mappers.getMapper(SchoolMapper.class);

	@Mapping(source = "schoolName", target = "schoolName")
	@Mapping(source = "campusName", target = "campusName")
	@Mapping(source = "schoolType", target = "schoolType")
	@Mapping(source = "link", target = "link")
	@Mapping(source = "schoolGubun", target = "schoolGubun")
	@Mapping(source = "adres", target = "adres")
	@Mapping(source = "region", target = "region")
	@Mapping(source = "estType", target = "estType")
	School toSchool(SchoolInfo schoolInfo);

	List<School> toSchoolData(List<SchoolInfo> request);
}
