package com.whooa.blog.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.category.dto.CategoryDto.CategorySearchRequest;
import com.whooa.blog.category.entity.CategoryEntity;

public interface CategoryQueryDslRepository {
	public abstract Page<CategoryEntity> search(CategorySearchRequest categorySearch, Pageable pageable);
}