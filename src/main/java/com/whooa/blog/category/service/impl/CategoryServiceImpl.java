package com.whooa.blog.category.service.impl;

import org.springframework.stereotype.Service;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.mapper.CategoryMapper;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.category.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	private CategoryRepository categoryRepository;
		
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public CategoryResponse create(CategoryCreateRequest categoryCreate) {
		CategoryEntity categoryEntity = categoryRepository.save(CategoryMapper.INSTANCE.toEntity(categoryCreate));
		
		return CategoryMapper.INSTANCE.toDto(categoryEntity);
	}
	
	
}