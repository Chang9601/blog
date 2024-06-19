package com.whooa.blog.file.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * @ConfigurationProperties 어노테이션은 application.properties 파일 혹은 application.yml 파일에 정의된 속성을 자동으로 POJO 클래스에 바인딩한다.
 * prefix 속성은 객체에 바인딩할 수 있는 속성의 접두사로 접두사가 file.upload인 모든 속성을 POJO 클래스의 필드에 바인딩한다. 애플리케이션 시작 시 작업을 수행한다.
 * 추가적인 파일 속성을 정의하면 클래스에 해당 필드를 추가하면 Spring Boot가 자동으로 필드를 속성과 바인딩한다.
 */
@ConfigurationProperties(prefix = "file.upload")
public class FileProperty {
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}