package com.whooa.blog.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;

import com.whooa.blog.util.PaginationUtil;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository categoryRepository;
		
	private PaginationUtil pagination;
	private Pageable pageable;
	
	@BeforeEach
	public void setUp() {		
		pagination = new PaginationUtil();
		pageable = pagination.makePageable();
	}
	
	@DisplayName("카테고리를 생성하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenCategoryEntity_whenCallSaveForCreate_thenReturnCategoryEntity(CategoryEntity categoryEntity) {
		CategoryEntity savedCategoryEntity;
		
		savedCategoryEntity = categoryRepository.save(categoryEntity);
		
		assertTrue(savedCategoryEntity.getId() > 0);
	}
	
	@DisplayName("카테고리를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForCreate_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			categoryRepository.save(null);
		});
	}
	
	@DisplayName("카테고리를 삭제하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenCategoryEntity_whenCallDelete_thenReturnNothing(CategoryEntity categoryEntity) {		
		CategoryEntity savedCategoryEntity;
		
		savedCategoryEntity = categoryRepository.save(categoryEntity);
		
		categoryRepository.delete(savedCategoryEntity);
		
		assertEquals(Optional.empty(), categoryRepository.findById(savedCategoryEntity.getId()));
	}
	
	@DisplayName("카테고리가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {				
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			categoryRepository.delete(null);
		});		
	}
	
	@DisplayName("카테고리를 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenId_whenCallFindById_thenReturnCategoryEntity(CategoryEntity categoryEntity) {		
		CategoryEntity foundCategoryEntity, savedCategoryEntity;
		
		savedCategoryEntity = categoryRepository.save(categoryEntity);
		foundCategoryEntity = categoryRepository.findById(savedCategoryEntity.getId()).get();

		assertEquals(savedCategoryEntity.getName(), foundCategoryEntity.getName());	
	}
	
	@DisplayName("카테고리를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			categoryRepository.findById(100L).get();
		});
	}
	
	@DisplayName("카테고리의 목록을 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenPagination_whenCallFindAll_thenReturnCategoryEntities(CategoryEntity categoryEntity1) {
		CategoryEntity categoryEntity2;
		Page<CategoryEntity> page;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		categoryRepository.save(categoryEntity1);
		categoryRepository.save(categoryEntity2);
		
		page = categoryRepository.findAll(pageable);

		assertEquals(2, page.getTotalElements());			
	}
	
	@DisplayName("카테고리를 수정하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("categoryParametersProvider")
	public void givenCategoryEntity_whenCallSaveForUpdate_thenReturnUpdatedCategoryEntity(CategoryEntity categoryEntity) {		
		CategoryEntity foundCategoryEntity, savedCategoryEntity, updatedCategoryEntity; 
		
		savedCategoryEntity = categoryRepository.save(categoryEntity);
		foundCategoryEntity = categoryRepository.findById(savedCategoryEntity.getId()).get();
		
		foundCategoryEntity.name("카테고리2");

		updatedCategoryEntity = categoryRepository.save(foundCategoryEntity);

		assertEquals(foundCategoryEntity.getName(), updatedCategoryEntity.getName());
	}
	
	@DisplayName("카테고리가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForUpdate_thenThrowInvalidDataAccessApiUsageException() {
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			categoryRepository.save(null);
		});
	}
	
	private static Stream<Arguments> categoryParametersProvider() {
		return Stream.of(Arguments.of(new CategoryEntity().name("카테고리1")));
	}
}