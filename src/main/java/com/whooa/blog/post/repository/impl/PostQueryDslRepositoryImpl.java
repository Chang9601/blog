package com.whooa.blog.post.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.whooa.blog.file.value.QFile;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.entity.QPostEntity;
import com.whooa.blog.post.repository.PostQueryDslRepository;
import com.whooa.blog.util.QueryDslUtil;

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
		List<OrderSpecifier> orders;
		QPostEntity postEntity;
		List<PostEntity> postEntities;
		List<Long> postEntityIds;
		
		postEntity = QPostEntity.postEntity;
		
		orders = getOrderSpecifier(pageable);
		
		postEntityIds = jpaQueryFactory
							.select(postEntity.id)
							.from(postEntity)
							.offset(pageable.getOffset())
							.limit(pageable.getPageSize())
							.orderBy(orders.stream().toArray(OrderSpecifier[]::new))
							.distinct()
							.fetch();
				
		postEntities = jpaQueryFactory
							.selectFrom(postEntity)
							.leftJoin(postEntity.comments)
							.fetchJoin()
							.where(postEntity.id.in(postEntityIds))
							.distinct()
							.fetch();
		
		postEntities = jpaQueryFactory
							.selectFrom(postEntity)
							.leftJoin(postEntity.files)
							.fetchJoin()
							.where(postEntity.id.in(postEntityIds))
							.distinct()
							.fetch();
		
		postEntities = jpaQueryFactory
							.selectFrom(postEntity)
							.leftJoin(postEntity.category)
							.fetchJoin()
							.where(postEntity.id.in(postEntityIds))
							.distinct()
							.fetch();

		countQuery = jpaQueryFactory
						.select(postEntity.countDistinct())
						.from(postEntity);
		
		return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<PostEntity> findAllByCategoryId(Long categoryId, Pageable pageable) {
		JPAQuery<Long> countQuery;
		QPostEntity postEntity;
		List<PostEntity> postEntities;
		List<Long> postEntityIds;
		
		postEntity = QPostEntity.postEntity;
		
		postEntityIds = jpaQueryFactory
							.select(postEntity.id)
							.from(postEntity)
							.where(postEntity.category.id.eq(categoryId))
							.offset(pageable.getOffset())
							.limit(pageable.getPageSize())
							.distinct()
							.fetch();
				
		postEntities = jpaQueryFactory
							.selectFrom(postEntity)
							.leftJoin(postEntity.comments)
							.fetchJoin()
							.where(postEntity.id.in(postEntityIds))
							.distinct()
							.fetch();

		postEntities = jpaQueryFactory
							.selectFrom(postEntity)
							.leftJoin(postEntity.files)
							.fetchJoin()
							.where(postEntity.id.in(postEntityIds))
							.distinct()
							.fetch();
		
		countQuery = jpaQueryFactory
						.select(postEntity.countDistinct())
						.from(postEntity);
		
		return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);	
	}
	
	private List<OrderSpecifier> getOrderSpecifier(Pageable pageable) {
		List<OrderSpecifier> orders = new ArrayList<>();
		
		if (pageable.getSort() != null) {
			for (Sort.Order order : pageable.getSort()) {
				Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
				
				switch (order.getProperty()) {
					case "id":
						OrderSpecifier<?> orderId = QueryDslUtil.getSorted(direction, QPostEntity.postEntity, "id");
						orders.add(orderId);
						break;
					case "title":
						OrderSpecifier<?> orderTitle = QueryDslUtil.getSorted(direction, QPostEntity.postEntity, "title");
						orders.add(orderTitle);
						break;
					default:
						break;
				}
			}
		}
		
		System.out.println(orders);
		
		return orders;
	}
}