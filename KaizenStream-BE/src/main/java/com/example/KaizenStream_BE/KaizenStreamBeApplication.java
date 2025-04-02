package com.example.KaizenStream_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.KaizenStream_BE")
@EnableCaching
@EnableAsync
public class KaizenStreamBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KaizenStreamBeApplication.class, args);
	}

}
