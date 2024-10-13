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
import com.whooa.blog.util.StringUtil;
import com.whooa.blog.util.PaginationParam;

@Service
public class CategoryServiceImpl implements CategoryService {
	private CategoryRepository categoryRepository;
		
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public CategoryResponse create(CategoryCreateRequest categoryCreate) {
		CategoryEntity categoryEntity;
		
		if (categoryRepository.existsByName(categoryCreate.getName())) {
			throw new DuplicateCategoryException(Code.CONFLICT, new String[] {"카테고리가 존재합니다."});
		}
		
		categoryEntity = CategoryMapper.INSTANCE.toEntity(categoryCreate);
		
		return CategoryMapper.INSTANCE.fromEntity(categoryRepository.save(categoryEntity));
	}

	@Override
	public void delete(Long id) {
		CategoryEntity categoryEntity;
		
		categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		categoryRepository.delete(categoryEntity);		
	}
	
	@Override
	public CategoryResponse find(Long id) {
		CategoryEntity categoryEntity;
		
		categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		
		return CategoryMapper.INSTANCE.fromEntity(categoryEntity);
	}

	@Override
	public PageResponse<CategoryResponse> findAll(PaginationParam pagination) {
		Pageable pageable;
		Page<CategoryEntity> page;
		List<CategoryEntity> categoryEntities;
		List<CategoryResponse> categoryResponse;
		int pageSize, pageNo, totalPages;
		long totalElements;
		boolean isLast, isFirst;
		
		pageable = pagination.makePageable();
		page = categoryRepository.findAll(pageable);
		
		categoryEntities = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
				
		categoryResponse = categoryEntities
								.stream()
								.map((categoryEntity) -> CategoryMapper.INSTANCE.fromEntity(categoryEntity))
								.collect(Collectors.toList());
		
		return PageResponse.handleResponse(categoryResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public CategoryResponse update(Long id, CategoryUpdateRequest categoryUpdate) {
		CategoryEntity categoryEntity;
		String name;
		
		categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		name = categoryUpdate.getName();
		
		if (StringUtil.notEmpty(name)) {
			categoryEntity.setName(name);
		}
		
		return CategoryMapper.INSTANCE.fromEntity(categoryRepository.save(categoryEntity));
	}
}