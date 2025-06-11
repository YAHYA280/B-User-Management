package com.marketingconfort.brainboost;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})

@ComponentScan(basePackages ={
		"com.marketingconfort.starter.core",
		"com.marketingconfort.brainboost.*"
})
@EnableJpaRepositories(basePackages = {
		"com.marketingconfort.starter.core.models",
		"com.marketingconfort.brainboost.repository"
})
@EntityScan(basePackages ={
		"com.marketingconfort.starter.core",
		"com.marketingconfort.brainboost_common.usermanagement.models",
})
@EnableJpaAuditing

public class Application {

	public static void main(String[] args) {
		SpringApplication.run(com.marketingconfort.brainboost.Application.class, args);
	}
}
