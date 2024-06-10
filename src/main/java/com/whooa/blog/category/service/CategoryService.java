package com.whooa.blog.category.service;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageQueryString;

public interface CategoryService {
	public abstract CategoryResponse create(CategoryCreateRequest categoryCreate);
	public abstract CategoryResponse find(Long id);
	public abstract PageResponse<CategoryResponse> findAll(PageQueryString pageDto);
	public abstract CategoryResponse update(Long id);
	public abstract CategoryResponse delete(Long id);
}
