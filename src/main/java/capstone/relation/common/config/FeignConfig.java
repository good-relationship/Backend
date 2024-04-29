package capstone.relation.common.config;

import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.QueryMapEncoder;
import feign.querymap.BeanQueryMapEncoder;

@Configuration
public class FeignConfig {
	@Bean
	public QueryMapEncoder queryMapEncoder() {
		return new BeanQueryMapEncoder() {
			@Override
			public Map<String, Object> encode(Object object) {
				Map<String, Object> queryMap = super.encode(object);
				// null 값 제거
				queryMap.values().removeIf(Objects::isNull);
				return queryMap;
			}
		};
	}
}
