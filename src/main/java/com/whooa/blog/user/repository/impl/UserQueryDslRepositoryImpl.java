package com.whooa.blog.user.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whooa.blog.user.dto.UserDto.UserSearchRequest;
import com.whooa.blog.user.entity.QUserEntity;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserQueryDslRepository;

@Repository
public class UserQueryDslRepositoryImpl extends QuerydslRepositorySupport implements UserQueryDslRepository {
	private JPAQueryFactory jpaQueryFactory;
	
	public UserQueryDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(UserEntity.class);
		
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<UserEntity> search(UserSearchRequest userSearch, Pageable pageable) {
		JPAQuery<Long> countQuery; 
		JPAQuery<UserEntity> jpaQuery;
		QUserEntity userEntity;
		List<UserEntity> userEntities;
		
		userEntity = QUserEntity.userEntity;
	
		jpaQuery = jpaQueryFactory
						.selectFrom(userEntity)
						.where(containsName(userSearch.getName()), isActive())
						.distinct();
		
		countQuery = jpaQueryFactory
						.select(userEntity.countDistinct())
						.from(userEntity)
						.where(containsName(userSearch.getName()), isActive());
		
		userEntities = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();

		return PageableExecutionUtils.getPage(userEntities, pageable, countQuery::fetchOne);		
	}
	
	private BooleanExpression containsName(String name) {
		return name != null ? QUserEntity.userEntity.name.containsIgnoreCase(name) : null;
	}
	
	private BooleanExpression isActive() {
		return QUserEntity.userEntity.active.isTrue();
	}
}