package com.example.KaizenStream_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.KaizenStream_BE")
public class KaizenStreamBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(KaizenStreamBeApplication.class, args);
	}

}
