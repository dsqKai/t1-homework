package ru.kai.homework.correction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class CorrectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CorrectionApplication.class, args);
	}

}
