package capstone.relation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableMongoRepositories
public class GoodRelationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodRelationApplication.class, args);
	}
}
