package capstone.relation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableFeignClients
@PropertySource("classpath:auth.properties")
@PropertySource("classpath:db.properties")
public class GoodRelationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodRelationApplication.class, args);
	}
}
