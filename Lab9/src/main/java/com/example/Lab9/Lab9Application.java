package com.example.Lab9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Lab9Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab9Application.class, args);
	}

}
