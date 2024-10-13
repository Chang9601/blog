package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDto.CategoryUpdateRequest;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.exception.DuplicateCategoryException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.category.service.impl.CategoryServiceImpl;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.util.PaginationParam;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryServiceTest {
	@InjectMocks
	private CategoryServiceImpl categoryServiceImpl;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private CategoryEntity categoryEntity1;
	
	private CategoryCreateRequest categoryCreate;
	private CategoryUpdateRequest categoryUpdate;
	private CategoryResponse category;
	
	@BeforeEach
	public void setUp() {
		categoryCreate = new CategoryCreateRequest();
		categoryCreate.setName("카테고리1");

		categoryUpdate = new CategoryUpdateRequest();
		categoryUpdate.setName("카테고리2");
		
		categoryEntity1 = new CategoryEntity();
		categoryEntity1.setName(categoryCreate.getName());
	}

	@DisplayName("카테고리를 생성하는데 성공한다.")
	@Test
	public void givenCategoryCreate_whenCallCreate_thenReturnCategory() {
		given(categoryRepository.save(any(CategoryEntity.class))).willReturn(categoryEntity1);
		given(categoryRepository.existsByName(any(String.class))).willReturn(false);

		category = categoryServiceImpl.create(categoryCreate);

		assertEquals(categoryEntity1.getName(), category.getName());

		then(categoryRepository).should(times(1)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).existsByName(any(String.class));

	}
	
	@DisplayName("카테고리를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {		
		assertThrows(NullPointerException.class, () -> {
			categoryServiceImpl.create(null);
		});

		then(categoryRepository).should(times(0)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(0)).existsByName(any(String.class));
	}

	@DisplayName("카테고리가 이미 존재하여 생성하는데 실패한다.")
	@Test
	public void givenCategoryCreate_whenCallCreate_thenThrowDuplicateCategoryException() {
		given(categoryRepository.existsByName(any(String.class))).willReturn(true);
		
		assertThrows(DuplicateCategoryException.class, () -> {
			categoryServiceImpl.create(categoryCreate);
		});
		
		then(categoryRepository).should(times(0)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).existsByName(any(String.class));

	}
	
	@DisplayName("카테고리를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		willDoNothing().given(categoryRepository).delete(any(CategoryEntity.class));
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity1));
		
		categoryServiceImpl.delete(categoryEntity1.getId());
		
		then(categoryRepository).should(times(1)).delete(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowCategoryNotFoundException() {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			categoryServiceImpl.delete(categoryEntity1.getId());
		});
		
		then(categoryRepository).should(times(0)).delete(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFind_thenReturnCategory() {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity1));

		category = categoryServiceImpl.find(categoryEntity1.getId());
		
		assertEquals(categoryEntity1.getName(), category.getName());
		
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFind_thenThrowCategoryNotFoundException() {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(CategoryNotFoundException.class, () -> {
			categoryServiceImpl.find(categoryEntity1.getId());
		});
		
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnCategories() {
		CategoryEntity categoryEntity2;
		PageResponse<CategoryResponse> page;
		
		categoryEntity2 = new CategoryEntity();
		categoryEntity2.setName("카테고리2");
		
		given(categoryRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<CategoryEntity>(List.of(categoryEntity1, categoryEntity2)));

		page = categoryServiceImpl.findAll(new PaginationParam());
		
		assertEquals(2, page.getTotalElements());
		
		then(categoryRepository).should(times(1)).findAll(any(Pageable.class));
	}
	
	@DisplayName("카테고리를 수정하는데 성공한다.")
	@Test
	public void givenCategoryUpdate_whenCallUpdate_thenReturnCategory() {
		CategoryEntity categoryEntity2;
		
		categoryEntity2 = new CategoryEntity();
		categoryEntity2.setName(categoryUpdate.getName());
			
		given(categoryRepository.save(any(CategoryEntity.class))).willReturn(categoryEntity2);
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity1));

		category = categoryServiceImpl.update(categoryEntity1.getId(), categoryUpdate);
		
		assertEquals(categoryEntity2.getName(), category.getName());
		
		then(categoryRepository).should(times(1)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenCategoryUpdate_whenCallUpdate_thenThrowCategoryNotFoundException() {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.empty());
				
		assertThrows(CategoryNotFoundException.class, () -> {
			categoryServiceImpl.update(categoryEntity1.getId(), categoryUpdate);
		});
		
		then(categoryRepository).should(times(0)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
}