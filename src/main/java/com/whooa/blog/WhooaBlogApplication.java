package com.whooa.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication 어노테이션은 다음 3개의 기능을 활성화한다.
// @EnableAutoConfiguration
// @ComponentScan
// @Configuration
@SpringBootApplication
public class WhooaBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhooaBlogApplication.class, args);
	}

}
