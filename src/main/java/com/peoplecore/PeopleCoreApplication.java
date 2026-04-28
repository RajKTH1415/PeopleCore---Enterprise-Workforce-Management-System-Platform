package com.peoplecore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(
		auditorAwareRef = "auditorAware"
)
@EnableAsync
@EntityScan("com.peoplecore.module")
@EnableJpaRepositories("com.peoplecore.repository")
@EnableScheduling
public class PeopleCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleCoreApplication.class, args);
	}

}
