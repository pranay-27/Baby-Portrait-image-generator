package com.baby.potrait.generator.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// Load .env variables
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())

        );
		
        SpringApplication.run(Application.class, args);
	}
}
