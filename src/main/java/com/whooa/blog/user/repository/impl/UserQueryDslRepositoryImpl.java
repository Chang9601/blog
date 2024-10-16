package com.whooa.blog.user.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.whooa.blog.user.dto.UserDto.UserSearchRequest;
import com.whooa.blog.user.entity.QUserEntity;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserQueryDslRepository;
import com.whooa.blog.util.StringUtil;

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
		JPAQuery<UserEntity> userEntityQuery;
		QUserEntity userEntity;
		List<UserEntity> userEntities;
		
		userEntity = QUserEntity.userEntity;
	
		userEntityQuery = jpaQueryFactory
							.selectFrom(userEntity)
							.where(buildConditions(userSearch))
							.distinct();
		
		countQuery = jpaQueryFactory
						.select(userEntity.countDistinct())
						.from(userEntity)
						.where(buildConditions(userSearch));
		
		userEntities = getQuerydsl().applyPagination(pageable, userEntityQuery).fetch();

		return PageableExecutionUtils.getPage(userEntities, pageable, countQuery::fetchOne);		
	}
	
	private BooleanBuilder buildConditions(UserSearchRequest userSearch) {
		return containsName(userSearch.getName()).or(containsEmail(userSearch.getEmail()));
	}
	
	private BooleanBuilder containsName(String name) {
		return StringUtil.notEmpty(name) ? new BooleanBuilder(QUserEntity.userEntity.name.containsIgnoreCase(name)) : new BooleanBuilder();
	}
	
	private BooleanBuilder containsEmail(String email) {
		return StringUtil.notEmpty(email) ? new BooleanBuilder(QUserEntity.userEntity.email.containsIgnoreCase(email)) : new BooleanBuilder();
	}
    
	// TODO: 이름이나 이메일이 없을 경우 or에서 항상 1
//	private BooleanBuilder isActive() {
//		return new BooleanBuilder(QUserEntity.userEntity.active.isTrue());
//	}
}