package com.example.KaizenStream_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.KaizenStream_BE")
@EnableCaching
@EnableAsync
@EnableScheduling
public class KaizenStreamBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KaizenStreamBeApplication.class, args);
	}

}
