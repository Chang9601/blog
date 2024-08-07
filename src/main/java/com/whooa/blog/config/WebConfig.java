package com.whooa.blog.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
//		resourceHandlerRegistry.addResourceHandler("/resources/**")
//								.addResourceLocations("/public/", "classpath:/templates/", "classpath:/static/")
//								.setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));
//	}
	
	private final long maxAge = 3600;
	
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
					.allowedOrigins("http://localhost:3000")
					.allowedMethods("GET", "POST")
					.allowedHeaders("*")
					.allowCredentials(true)
					.maxAge(maxAge);
		
		WebMvcConfigurer.super.addCorsMappings(corsRegistry);
	}
}