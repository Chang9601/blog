package com.whooa.blog.comment.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.whooa.blog.comment.dto.CommentDto.CommentSearchRequest;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.entity.QCommentEntity;
import com.whooa.blog.comment.repository.CommentQueryDslRepository;

@Repository
public class CommentQueryDslRepositoryImpl extends QuerydslRepositorySupport implements CommentQueryDslRepository {
	private JPAQueryFactory jpaQueryFactory;
	
	public CommentQueryDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(CommentEntity.class);
		
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<CommentEntity> search(CommentSearchRequest commentSearch, Pageable pageable) {
		JPAQuery<Long> countQuery; 
		JPAQuery<CommentEntity> jpaQuery;
		QCommentEntity commentEntity;
		List<CommentEntity> commentEntities;
		
		commentEntity = QCommentEntity.commentEntity;
		
		jpaQuery = jpaQueryFactory
						.selectFrom(commentEntity)
						.where(containsContent(commentSearch.getContent()))
						.distinct();
		
		countQuery = jpaQueryFactory
						.select(commentEntity.countDistinct())
						.where(containsContent(commentSearch.getContent()));
		
		commentEntities = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();
		
		return PageableExecutionUtils.getPage(commentEntities, pageable, countQuery::fetchOne);
	}
	
	private BooleanExpression containsContent(String content) {
		return content != null ? QCommentEntity.commentEntity.content.containsIgnoreCase(name) : null;
	}
}