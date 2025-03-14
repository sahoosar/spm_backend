package com.spm.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication (scanBasePackages = {"com.spm.*"})
public class SpmApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpmApplication.class, args);
	}

}
