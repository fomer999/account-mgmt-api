package com.lf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;

@SpringBootApplication
// Remove the Spring Security classes from boot, as this is handled in the OAuth-Server
@EnableAutoConfiguration
		(exclude = {
				SecurityAutoConfiguration.class,
				ManagementWebSecurityAutoConfiguration.class})
public class LfCdnApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(LfCdnApiApplication.class, args);
	}
}
