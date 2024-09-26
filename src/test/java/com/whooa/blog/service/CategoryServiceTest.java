package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import com.whooa.blog.util.PaginationUtil;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryServiceTest {
	@Mock
	private CategoryRepository categoryRepository;
	
	@InjectMocks
	private CategoryServiceImpl categoryServiceImpl;
		
	private CategoryCreateRequest categoryCreate;
	private CategoryUpdateRequest categoryUpdate;
	private CategoryResponse category;

	private PaginationUtil pagination;
	
	@BeforeAll
	public void setUp() {
		categoryCreate = new CategoryCreateRequest().name("카테고리1");
		categoryUpdate = new CategoryUpdateRequest().name("카테고리2");
				
		pagination = new PaginationUtil();		
	}
	

	@DisplayName("카테고리를 생성하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenCategoryCreate_whenCallCreate_thenReturnCategory(CategoryEntity categoryEntity) {
		given(categoryRepository.save(any(CategoryEntity.class))).willReturn(categoryEntity);
		given(categoryRepository.existsByName(any(String.class))).willReturn(false);
		
		category = categoryServiceImpl.create(categoryCreate);

		assertEquals(categoryEntity.getName(), category.getName());

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
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenId_whenCallDelete_thenReturnNothing(CategoryEntity categoryEntity) {
		willDoNothing().given(categoryRepository).delete(any(CategoryEntity.class));
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity));
		
		categoryServiceImpl.delete(categoryEntity.getId());
		
		then(categoryRepository).should(times(1)).delete(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenNull_whenCallDelete_thenThrowCategoryNotFoundException(CategoryEntity categoryEntity) {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			categoryServiceImpl.delete(categoryEntity.getId());
		});
		
		then(categoryRepository).should(times(0)).delete(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리를 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenId_whenCallFind_thenReturnCategory(CategoryEntity categoryEntity) {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity));
		
		category = categoryServiceImpl.find(categoryEntity.getId());
		
		assertEquals(categoryEntity.getName(), category.getName());
		
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리를 조회하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenId_whenCallFind_thenThrowCategoryNotFoundException(CategoryEntity categoryEntity) {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(CategoryNotFoundException.class, () -> {
			categoryServiceImpl.find(categoryEntity.getId());
		});
		
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리 목록을 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenPagination_whenCallFindAll_thenReturnCategories(CategoryEntity categoryEntity1) {
		CategoryEntity categoryEntity2;
		PageResponse<CategoryResponse> page;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		given(categoryRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<CategoryEntity>(List.of(categoryEntity1, categoryEntity2)));
		
		page = categoryServiceImpl.findAll(pagination);
		
		assertEquals(2, page.getTotalElements());
		
		then(categoryRepository).should(times(1)).findAll(any(Pageable.class));
	}
	
	@DisplayName("카테고리를 수정하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenCategoryUpdate_whenCallUpdate_thenReturnCategory(CategoryEntity categoryEntity1) {
		CategoryEntity categoryEntity2;
		
		categoryEntity2 = new CategoryEntity().name(categoryUpdate.getName());

		given(categoryRepository.save(any(CategoryEntity.class))).willReturn(categoryEntity2);
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity1));
				
		category = categoryServiceImpl.update(categoryEntity1.getId(), categoryUpdate);
		
		assertEquals(categoryEntity2.getName(), category.getName());
		
		then(categoryRepository).should(times(1)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenCategoryUpdate_whenCallUpdate_thenThrowCategoryNotFoundException(CategoryEntity categoryEntity) {
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.empty());
				
		assertThrows(CategoryNotFoundException.class, () -> {
			categoryServiceImpl.update(categoryEntity.getId(), categoryUpdate);
		});
		
		then(categoryRepository).should(times(0)).save(any(CategoryEntity.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	private static Stream<Arguments> categoryParametersProvider() {
		CategoryEntity categoryEntity = new CategoryEntity().name("카테고리1");
		
		return Stream.of(Arguments.of(categoryEntity));
	}		
}