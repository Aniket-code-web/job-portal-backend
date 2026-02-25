package com.aniket.Job_portal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableScheduling
@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
public class JobPortalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPortalBackendApplication.class, args);
	}

}
