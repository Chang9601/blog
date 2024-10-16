package com.whooa.blog.category.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whooa.blog.category.dto.CategoryDto.CategorySearchRequest;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.entity.QCategoryEntity;
import com.whooa.blog.category.repository.CategoryQueryDslRepository;

@Repository
public class CategoryQueryDslRepositoryImpl extends QuerydslRepositorySupport implements CategoryQueryDslRepository {
	private JPAQueryFactory jpaQueryFactory;

	public CategoryQueryDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(CategoryEntity.class);

		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<CategoryEntity> search(CategorySearchRequest categorySearch, Pageable pageable) {
		JPAQuery<Long> countQuery; 
		JPAQuery<CategoryEntity> categoryEntityQuery;
		QCategoryEntity categoryEntity;
		List<CategoryEntity> categoryEntities;
		
		categoryEntity = QCategoryEntity.categoryEntity;
		
		categoryEntityQuery = jpaQueryFactory
								.selectFrom(categoryEntity)
								.where(containsName(categorySearch.getName()))
								.distinct();
		
		countQuery = jpaQueryFactory
						.select(categoryEntity.countDistinct())
						.from(categoryEntity)
						.where(containsName(categorySearch.getName()));
		
		categoryEntities = getQuerydsl().applyPagination(pageable, categoryEntityQuery).fetch();
		
		return PageableExecutionUtils.getPage(categoryEntities, pageable, countQuery::fetchOne);
	}
	
	private BooleanExpression containsName(String name) {
		return name != null ? QCategoryEntity.categoryEntity.name.containsIgnoreCase(name) : null;
	}
}