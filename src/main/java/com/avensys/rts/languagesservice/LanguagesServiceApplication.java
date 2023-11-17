package com.avensys.rts.languagesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LanguagesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LanguagesServiceApplication.class, args);
	}

}
