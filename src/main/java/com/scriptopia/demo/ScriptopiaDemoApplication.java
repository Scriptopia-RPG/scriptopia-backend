package com.scriptopia.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ScriptopiaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScriptopiaDemoApplication.class, args);
	}

}
