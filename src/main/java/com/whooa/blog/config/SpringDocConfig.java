package com.whooa.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class SpringDocConfig {
	
	@Bean
	public OpenAPI openAPI(@Value("${springdoc.version}") String version) {
		return new OpenAPI()
				.components(components())
				.info(info(version))
				.externalDocs(externalDocumentation());
	}
	
	private Components components() {
		return new Components()
				.addSecuritySchemes("JWT Cookie Authentication", new SecurityScheme().type(Type.APIKEY).in(In.COOKIE).name("ACCESS_TOKEN"));
	}
	
	private Info info(String version) {
		return new Info()
				.title("Spring Boot 블로그")
				.description("Spring Boot 블로그 API")
				.version(version)
				.contact(new Contact().name("이창섭").email("changsup96@naver.com"))
				.license(new License().name("MIT License"));
	}
	
	private ExternalDocumentation externalDocumentation() {
		return new ExternalDocumentation()
				.description("Spring Boot 블로그 API")
				.url("https://github.com/Chang9601/blog");
	}
}