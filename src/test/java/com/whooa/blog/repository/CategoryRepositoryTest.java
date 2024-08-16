package com.whooa.blog.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
	
	private CategoryEntity categoryEntity1;
	
	private PaginationUtil pagination;
	private Pageable pageable;
	
	@BeforeEach
	public void setUp() {
		categoryEntity1 = new CategoryEntity().name("카테고리1");
		
		pagination = new PaginationUtil();
		pageable = pagination.makePageable();
	}
	
	@DisplayName("카테고리를 생성하는데 성공한다.")
	@Test
	public void givenCategoryEntity_whenCallSaveForCreate_thenReturnCategoryEntity() {
		CategoryEntity savedCategoryEntity;
		
		savedCategoryEntity = categoryRepository.save(categoryEntity1);
		
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
	@Test
	public void givenCategoryEntity_whenCallDelete_thenReturnNothing() {		
		CategoryEntity savedCategoryEntity;
		
		savedCategoryEntity = categoryRepository.save(categoryEntity1);
		
		categoryRepository.delete(savedCategoryEntity);
		
		assertEquals(categoryRepository.findById(savedCategoryEntity.getId()), Optional.empty());
	}
	
	@DisplayName("카테고리가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {				
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			categoryRepository.delete(null);
		});		
	}
	
	@DisplayName("카테고리를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindById_thenReturnCategoryEntity() {		
		CategoryEntity foundCategoryEntity, savedCategoryEntity;
		
		savedCategoryEntity = categoryRepository.save(categoryEntity1);
		foundCategoryEntity = categoryRepository.findById(savedCategoryEntity.getId()).get();

		assertEquals(foundCategoryEntity.getName(), savedCategoryEntity.getName());			
	}
	
	@DisplayName("카테고리를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			categoryRepository.findById(100L).get();
		});
	}
	
	@DisplayName("카테고리의 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnCategoryEntities() {
		CategoryEntity categoryEntity2;
		Page<CategoryEntity> page;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		categoryRepository.save(categoryEntity1);
		categoryRepository.save(categoryEntity2);
		
		page = categoryRepository.findAll(pageable);

		assertEquals(page.getTotalElements(), 2);			
	}
	
	@DisplayName("카테고리를 수정하는데 성공한다.")
	@Test
	public void givenCategoryEntity_whenCallSaveForUpdate_thenReturnUpdatedCategoryEntity() {		
		CategoryEntity  foundCategoryEntity, savedCategoryEntity, updatedCategoryEntity; 
		
		savedCategoryEntity = categoryRepository.save(categoryEntity1);
		foundCategoryEntity = categoryRepository.findById(savedCategoryEntity.getId()).get();
		
		foundCategoryEntity.name("카테고리2");

		updatedCategoryEntity = categoryRepository.save(foundCategoryEntity);

		assertEquals(updatedCategoryEntity.getName(), foundCategoryEntity.getName());
	}
	
	@DisplayName("카테고리가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForUpdate_thenThrowInvalidDataAccessApiUsageException() {
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			categoryRepository.save(null);
		});		
	}
}