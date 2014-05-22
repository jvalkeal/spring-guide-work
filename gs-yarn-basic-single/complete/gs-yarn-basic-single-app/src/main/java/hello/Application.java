package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.yarn.YarnSystemConstants;
import org.springframework.yarn.client.YarnClient;

@Configuration
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		if (context.containsBean(YarnSystemConstants.DEFAULT_ID_CLIENT)) {
			context.getBean(YarnClient.class).submitApplication();
		}
	}

	@Bean
	@Profile("container")
	public HelloPojo helloPojo() {
		return new HelloPojo();
	}

}
