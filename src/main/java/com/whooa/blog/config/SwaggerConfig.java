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
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openApi(@Value("${swagger.version}") String version) {
		return new OpenAPI()
					.components(createComponents())
					.info(createInfo(version))
					.externalDocs(createExternalDocumentation());
	}
	
	private Components createComponents() {
		return new Components()
					.addSecuritySchemes("JWT Cookie Authentication", createJwtCookieScheme())
					.addSecuritySchemes("OAuth 2.0 Authorization", createOAuth2Scheme());
	}
	
	private SecurityScheme createJwtCookieScheme() {
		return new SecurityScheme().type(Type.APIKEY).in(In.COOKIE).name("access_token");
	}
	
	private SecurityScheme createOAuth2Scheme() {
		OAuthFlows oAuthFlows = createOAuth2Flows();
		
		return new SecurityScheme()
					.type(Type.OAUTH2)
					.description("authorization code 흐름을 사용하는 OAuth 2.0")
					.flows(oAuthFlows);
	}
	
	private OAuthFlows createOAuth2Flows() {
		OAuthFlow oAuthFlow = createOAuth2AuthorizationCodeFlow();
		
		return new OAuthFlows().authorizationCode(oAuthFlow);
	}
	
	private OAuthFlow createOAuth2AuthorizationCodeFlow() {
		return new OAuthFlow()
					.authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
					.tokenUrl("https://www.googleapis.com/oauth2/v4/token")
					.scopes(new Scopes().addString("email", "이메일").addString("profile", "프로필"))
					.authorizationUrl("https://nid.naver.com/oauth2.0/authorize")
					.tokenUrl("https://nid.naver.com/oauth2.0/token")
					.scopes(new Scopes().addString("email", "이메일").addString("name", "이름"));
	}
	
	private Info createInfo(String version) {
		return new Info()
					.title("Spring Boot 블로그")
					.description("Spring Boot 블로그 API")
					.version(version)
					.contact(new Contact().name("이창섭").email("changsup96@naver.com"))
					.license(new License().name("MIT License"));
	}
	
	private ExternalDocumentation createExternalDocumentation() {
		return new ExternalDocumentation()
					.description("Spring Boot 블로그 API")
					.url("https://github.com/Chang9601/blog");
	}
}