package com.whooa.blog.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.mapper.CategoryMapper;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.category.service.CategoryService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageQueryString;

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

	@Override
	public CategoryResponse find(Long id) {
		CategoryEntity categoryEntity = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		
		return CategoryMapper.INSTANCE.toDto(categoryEntity);
	}

	@Override
	public PageResponse<CategoryResponse> findAll(PageQueryString pageDto) {
		String sortBy = pageDto.getSortBy();		
		Sort sort = pageDto.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(pageDto.getPageNo(), pageDto.getPageSize(), sort);
		
		Page<CategoryEntity> categories = categoryRepository.findAll(pageable);
		
		List<CategoryEntity> categoryEntities = categories.getContent();
		int pageSize = categories.getSize();
		int pageNo = categories.getNumber();
		long totalElements = categories.getTotalElements();
		int totalPages = categories.getTotalPages();
		boolean isLast = categories.isLast();
		boolean isFirst = categories.isFirst();
				
		List<CategoryResponse> categoryResponse = categoryEntities.stream().map((categoryEntity) -> CategoryMapper.INSTANCE.toDto(categoryEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(categoryResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public CategoryResponse update(Long id) {
		return null;
	}

	@Override
	public CategoryResponse delete(Long id) {
		return null;
	}
}