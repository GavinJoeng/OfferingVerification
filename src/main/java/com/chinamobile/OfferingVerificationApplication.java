package com.chinamobile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.chinamobile.mapper")
@SpringBootApplication
public class OfferingVerificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfferingVerificationApplication.class, args);
	}
}
