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

import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.entity.QCommentEntity;
import com.whooa.blog.comment.param.CommentSearchParam;
import com.whooa.blog.comment.repository.CommentQueryDslRepository;

@Repository
public class CommentQueryDslRepositoryImpl extends QuerydslRepositorySupport implements CommentQueryDslRepository {
	private JPAQueryFactory commentEntityQueryFactory;
	
	public CommentQueryDslRepositoryImpl(JPAQueryFactory commentEntityQueryFactory) {
		super(CommentEntity.class);
		
		this.commentEntityQueryFactory = commentEntityQueryFactory;
	}

	@Override
	public Page<CommentEntity> searchAll(CommentSearchParam commentSearchParam, Pageable pageable) {
		JPAQuery<Long> countQuery; 
		JPAQuery<CommentEntity> commentEntityQuery;
		QCommentEntity commentEntity;
		List<CommentEntity> commentEntities;
		
		commentEntity = QCommentEntity.commentEntity;
		
		commentEntityQuery = commentEntityQueryFactory
								.selectFrom(commentEntity)
								.where(containsContent(commentSearchParam.getContent()))
								.distinct();
		
		countQuery = commentEntityQueryFactory
								.select(commentEntity.countDistinct())
								.from(commentEntity)
								.where(containsContent(commentSearchParam.getContent()));
		
		commentEntities = getQuerydsl().applyPagination(pageable, commentEntityQuery).fetch();
		
		return PageableExecutionUtils.getPage(commentEntities, pageable, countQuery::fetchOne);
	}
	
	private BooleanExpression containsContent(String content) {
		return content != null ? QCommentEntity.commentEntity.content.containsIgnoreCase(content) : null;
	}
}