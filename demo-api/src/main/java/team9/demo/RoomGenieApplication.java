package team9.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("team9.demo.external.config.properties")
public class RoomGenieApplication {
	public static void main(String[] args) {
		SpringApplication.run(RoomGenieApplication.class, args);
	}
}