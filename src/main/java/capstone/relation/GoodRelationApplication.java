package capstone.relation;

import org.bson.Document;
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
		// String connectionString = "mongodb+srv://wnddms12345:<db_password>@clustergoodrelation.0qglt.mongodb.net/?retryWrites=true&w=majority&appName=ClusterGoodRelation";
		// ServerApi serverApi = ServerApi.builder()
		// 	.version(ServerApiVersion.V1)
		// 	.build();
		// MongoClientSettings settings = MongoClientSettings.builder()
		// 	.applyConnectionString(new ConnectionString(connectionString))
		// 	.serverApi(serverApi)
		// 	.build();
		// // Create a new client and connect to the server
		// try (MongoClient mongoClient = MongoClients.create(settings)) {
		// 	try {
		// 		// Send a ping to confirm a successful connection
		// 		MongoDatabase database = mongoClient.getDatabase("admin");
		// 		database.runCommand(new Document("ping", 1));
		// 		System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
		// 	} catch (MongoException e) {
		// 		e.printStackTrace();
		// 	}
		// }
	}
}
