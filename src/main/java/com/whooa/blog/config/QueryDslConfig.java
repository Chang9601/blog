package com.whooa.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class QueryDslConfig {
	/* 엔티티 매니저를 빈으로 주입 시 사용하는 어노테이션 */
	@PersistenceContext
	private EntityManager entityManager;
	
	public QueryDslConfig(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
}