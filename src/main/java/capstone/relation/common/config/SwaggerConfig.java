package capstone.relation.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("조은사이 API Document")
			.version("1.0")
			.description(
				"환영합니다! [조은사이](https://example.com)는 팀프로젝트를 할 때 사용하는 도구를 하나로 모아둔 플랫폼입니다. 이 API 문서는 조은사이의 API를 사용하는 방법을 설명합니다.\n")
			.contact(new io.swagger.v3.oas.models.info.Contact().email("wnddms12345@gmail.com"));

		String jwtScheme = "jwtAuth";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtScheme);
		Components components = new Components()
			.addSecuritySchemes(jwtScheme, new SecurityScheme()
				.name("Authorization")
				.type(SecurityScheme.Type.HTTP)
				.in(SecurityScheme.In.HEADER)
				.scheme("Bearer")
				.bearerFormat("JWT"));

		return new OpenAPI()
			.addServersItem(new Server().url("https://joeun.duckdns.org"))
			.addServersItem(new Server().url("http://localhost:8080"))
			.components(new Components())
			.info(info)
			.addSecurityItem(securityRequirement)
			.components(components);
	}
}

