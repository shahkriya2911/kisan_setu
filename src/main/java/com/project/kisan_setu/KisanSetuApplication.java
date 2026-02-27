package com.project.kisan_setu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KisanSetuApplication {

	public static void main(String[] args) {
		SpringApplication.run(KisanSetuApplication.class, args);
	}

}
