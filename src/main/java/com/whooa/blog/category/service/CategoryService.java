package com.whooa.blog.category.service;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDto.CategoryUpdateRequest;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.util.PaginationParam;

public interface CategoryService {
	public abstract CategoryResponse create(CategoryCreateRequest categoryCreate);
	public abstract void delete(Long id);
	public abstract CategoryResponse find(Long id);
	public abstract PageResponse<CategoryResponse> findAll(PaginationParam paginationUtil);
	public abstract CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdate);
}
