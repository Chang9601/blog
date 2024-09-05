//package com.whooa.blog.integration;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.annotation.Bean;
//import org.testcontainers.containers.MySQLContainer;
//
//@TestConfiguration
//public class MySqlContainer {
//
//	/*
//	 * Spring 3.1 이전에는 Docker를 실행하고 데이터베이스 컨테이를 실행한 다음 테스트를 진행해야 했다. 
//	 * 하지만 Spring 3.1 이후로 애플리케이션과 함께 시작할 컨테이너의 정의를 포함한 @TestConfiguration 클래스를 사용해서 컨테이너를 실행할 필요가 없다.
//	 */
//	@Bean
//	public MySQLContainer<?> mySqlContainer() {
//		MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.33");
//		
//		mySqlContainer.start();
//		
//		return mySqlContainer;
//	}
//}