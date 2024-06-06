package com.whooa.blog.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDto.CategoryUpdateRequest;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.exception.DuplicateCategoryException;
import com.whooa.blog.category.mapper.CategoryMapper;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.category.service.CategoryService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.util.NotNullNotEmptyChecker;
import com.whooa.blog.util.PaginationUtil;

@Service
public class CategoryServiceImpl implements CategoryService {
	private CategoryRepository categoryRepository;
		
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public CategoryResponse create(CategoryCreateRequest categoryCreate) {
		if (categoryRepository.existsByName(categoryCreate.getName())) {
			throw new DuplicateCategoryException(Code.CONFLICT, new String[] {"카테고리가 존재합니다."});
		}
		
		CategoryEntity categoryEntity = categoryRepository.save(CategoryMapper.INSTANCE.toEntity(categoryCreate));
		
		return CategoryMapper.INSTANCE.toDto(categoryEntity);
	}

	@Override
	public void delete(Long id) {
		CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		categoryRepository.delete(categoryEntity);		
	}
	
	@Override
	public CategoryResponse find(Long id) {
		CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		
		return CategoryMapper.INSTANCE.toDto(categoryEntity);
	}

	@Override
	public PageResponse<CategoryResponse> findAll(PaginationUtil pagination) {
		Pageable pageable = pagination.makePageable();
		
		Page<CategoryEntity> page = categoryRepository.findAll(pageable);
		
		List<CategoryEntity> categoryEntities = page.getContent();
		int pageSize = page.getSize();
		int pageNo = page.getNumber();
		long totalElements = page.getTotalElements();
		int totalPages = page.getTotalPages();
		boolean isLast = page.isLast();
		boolean isFirst = page.isFirst();
				
		List<CategoryResponse> categoryResponse = categoryEntities.stream().map((categoryEntity) -> CategoryMapper.INSTANCE.toDto(categoryEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(categoryResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdate) {
		CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		
		String name = categoryUpdate.getName();
		
		if (NotNullNotEmptyChecker.check(name)) {
			categoryEntity.name(name);
		}
		
		return CategoryMapper.INSTANCE.toDto(categoryRepository.save(categoryEntity));
	}

}