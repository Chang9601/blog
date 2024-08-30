package com.whooa.blog.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.whooa.blog.post.entity.QPostEntity;

public class QueryDslUtil {
	public static OrderSpecifier<?> getSorted(Order order, Path<?> parent, String field) {
		Path<Object> path = Expressions.path(Object.class, parent, field);
		
		return new OrderSpecifier(order, path);
	}
}