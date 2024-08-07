package com.whooa.blog.category.service;

import com.whooa.blog.category.dto.CategoryDTO.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDTO.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDTO.CategoryUpdateRequest;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.util.PaginationUtil;

public interface CategoryService {
	public abstract CategoryResponse create(CategoryCreateRequest categoryCreate);
	public abstract void delete(Long id);
	public abstract CategoryResponse find(Long id);
	public abstract PageResponse<CategoryResponse> findAll(PaginationUtil paginationUtil);
	public abstract CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdate);
}
