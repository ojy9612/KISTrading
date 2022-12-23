package com.example.kistrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // 스케줄러 적용
@EnableJpaAuditing // createAt
public class KisTradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(KisTradingApplication.class, args);
	}

}
