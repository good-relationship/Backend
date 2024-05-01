package capstone.relation.workspace.school.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import capstone.relation.workspace.school.SchoolMapper;
import capstone.relation.workspace.school.client.SchoolInfoClient;
import capstone.relation.workspace.school.domain.School;
import capstone.relation.workspace.school.dto.SchoolInfoRequest;
import capstone.relation.workspace.school.dto.response.SchoolInfoResponse;
import capstone.relation.workspace.school.respository.SchoolRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolService {
	private final SchoolInfoClient schoolInfoClient;
	private final SchoolRepository schoolRepository;
	@Value("${school.api.key}")
	private String apiKey;

	public List<School> searchSchool(String name) {
		return schoolRepository.findBySchoolNameContaining(name);
	}

	public Optional<School> getSchoolEntity(String name) {
		return schoolRepository.findByName(name);
	}

	public void initSchool() {
		SchoolInfoRequest request = new SchoolInfoRequest(apiKey);
		SchoolInfoResponse schoolInfo = schoolInfoClient.getSchoolInfo(request);
		List<School> schoolData = SchoolMapper.INSTANCE.toSchoolData(schoolInfo.getDataSearch().getContent());
		schoolRepository.saveAll(schoolData);
	}
}
