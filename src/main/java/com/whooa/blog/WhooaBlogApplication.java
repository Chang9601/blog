package com.whooa.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.whooa.blog.file.property.FileProperty;

/*
 * @SpringBootApplication 어노테이션은 다음 3개의 어노테이션을 활성화한다.
 * 1. @EnableAutoConfiguration
 * 2. @ComponentScan
 * 3. @Configuration
 * 
 * @EnableConfigurationProperties 어노테이션은 ConfigurationProperties 기능을 활성화한다.
 */
@EnableConfigurationProperties({
	FileProperty.class
})
@SpringBootApplication
public class WhooaBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhooaBlogApplication.class, args);
	}

}
