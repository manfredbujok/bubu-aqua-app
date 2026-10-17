package tech.taiiku.controlapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ControlApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlApiApplication.class, args);
	}

}
