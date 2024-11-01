package ru.kai.homework.correction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class CorrectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorrectionApplication.class, args);
	}

}
