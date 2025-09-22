package com.cinema.imax_eureka_config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
public class ImaxEurekaConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImaxEurekaConfigApplication.class, args);
	}

}
