package capstone.relation.workspace.school.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import capstone.relation.global.config.FeignConfig;
import capstone.relation.workspace.school.dto.SchoolInfoRequest;
import capstone.relation.workspace.school.dto.response.SchoolInfoResponse;
import lombok.extern.slf4j.Slf4j;

@FeignClient(name = "SchoolInfoClient",
	url = "${school.info.url}",
	configuration = FeignConfig.class,
	fallbackFactory = SchoolInfoClient.SchoolInfoClientFallback.class
)

public interface SchoolInfoClient {
	@GetMapping
	SchoolInfoResponse getSchoolInfo(@SpringQueryMap SchoolInfoRequest schoolInfoRequest);

	@Slf4j
	@Component
	class SchoolInfoClientFallback implements FallbackFactory<SchoolInfoClient> {
		@Override
		public SchoolInfoClient create(Throwable cause) {
			log.warn("SchoolInfoClient 오류 {}", cause.getMessage());
			throw new IllegalArgumentException(cause.getMessage());
		}
	}

}
