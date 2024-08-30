package com.whooa.blog.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.param.CategorySearchParam;

public interface CategoryQueryDslRepository {
	public abstract Page<CategoryEntity> searchAll(CategorySearchParam categorySearchParam, Pageable pageable);
}