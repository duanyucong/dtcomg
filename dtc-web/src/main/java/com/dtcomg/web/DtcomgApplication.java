package com.dtcomg.web;

import org.springframework.boot.SpringApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.dtcomg")
@MapperScan("com.dtcomg.system.mapper")
public class DtcomgApplication {

	public static void main(String[] args) {
		SpringApplication.run(DtcomgApplication.class, args);
	}

}
