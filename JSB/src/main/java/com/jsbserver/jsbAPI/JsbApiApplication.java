package com.jsbserver.jsbAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JsbApiApplication {
	public static void main(String[] args) {
		try {
			SpringApplication.run(JsbApiApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
