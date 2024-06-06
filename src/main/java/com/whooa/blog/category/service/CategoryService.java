package com.whooa.blog.category.service;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;

public interface CategoryService {
	public abstract CategoryResponse create(CategoryCreateRequest categoryCreate);
}
