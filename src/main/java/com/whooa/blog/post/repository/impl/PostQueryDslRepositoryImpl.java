package com.whooa.blog.post.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.whooa.blog.comment.entity.QCommentEntity;
import com.whooa.blog.file.value.QFile;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.entity.QPostEntity;
import com.whooa.blog.post.repository.PostQueryDslRepository;

@Repository
public class PostQueryDslRepositoryImpl extends QuerydslRepositorySupport implements PostQueryDslRepository {
	private JPAQueryFactory jpaQueryFactory;

	public PostQueryDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		super(PostEntity.class);
		
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<PostEntity> findAll(Pageable pageable) {
		JPAQuery<Long> countQuery;
		JPAQuery<PostEntity> jpaQuery;
		QPostEntity postEntity;
		QCommentEntity comments;
		QFile files;
		List<PostEntity> postEntities;
		
		files = QFile.file;
		comments = QCommentEntity.commentEntity;
		postEntity = QPostEntity.postEntity;
		
		jpaQuery = jpaQueryFactory
						.selectFrom(postEntity)
						.leftJoin(postEntity.comments)
						.fetchJoin()
						.leftJoin(postEntity.files)
						.fetchJoin()
						.distinct();
		
		countQuery = jpaQueryFactory
						.select(postEntity.countDistinct())
						.from(postEntity)
						.leftJoin(postEntity.comments)
						.fetchJoin()
						.leftJoin(postEntity.files)
						.fetchJoin();
		
		postEntities = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();
		
		return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);
	}
}