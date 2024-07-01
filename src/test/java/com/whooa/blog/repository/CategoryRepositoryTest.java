package com.whooa.blog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.util.PaginationUtil;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository categoryRepository;
	
	private CategoryEntity categoryEntity1;
	
	private PaginationUtil paginationUtil;
	private Pageable pageable;
	
	@BeforeEach
	public void setUp() {
		categoryEntity1 = new CategoryEntity().name("테스트 카테고리");
		
		paginationUtil = new PaginationUtil();
		pageable = paginationUtil.makePageable();
	}
	
	@DisplayName("카테고리를 생성하는데 성공한다.")
	@Test
	public void givenCategoryEntity_whenCallSave_thenReturnCategoryEntity() {
		CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity1);
		
		assertNotNull(savedCategoryEntity);
		assertTrue(savedCategoryEntity.getId() > 0);
	}
	
	@DisplayName("카테고리가 존재하지 않아 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSave_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			categoryRepository.save(null);
		});
	}
	
	@DisplayName("카테고리의 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnCategoryEntities() {
		CategoryEntity categoryEntity2 = new CategoryEntity().name("실전 카테고리");
		
		categoryRepository.save(categoryEntity1);
		categoryRepository.save(categoryEntity2);
		
		Page<CategoryEntity> categories = categoryRepository.findAll(pageable);
		List<CategoryEntity> categoryEntities = categories.getContent();

		assertEquals(categoryEntities.size(), 2);			
	}
	
	@DisplayName("카테고리를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindById_thenReturnCategoryEntity() {		
		CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity1);
		CategoryEntity foundCategoryEntity = categoryRepository.findById(savedCategoryEntity.getId()).get();

		assertNotNull(foundCategoryEntity);
		assertEquals(foundCategoryEntity.getName(), savedCategoryEntity.getName());			
	}
	
	@DisplayName("카테고리가 존재하지 않아서 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			categoryRepository.findById(1L).get();
		});
	}

	@DisplayName("카테고리를 갱신하는데 성공한다.")
	@Test
	public void givenCategoryEntity_whenCallSave_thenReturnUpdatedCategoryEntity() {		
		CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity1);
		CategoryEntity foundCategoryEntity = categoryRepository.findById(savedCategoryEntity.getId()).get();
		
		foundCategoryEntity.name("실전 카테고리");

		CategoryEntity updatedCategoryEntity = categoryRepository.save(foundCategoryEntity);

		assertEquals(updatedCategoryEntity.getName(), foundCategoryEntity.getName());
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenCategoryEntity_whenCallDelete_thenReturnNothing() {		
		CategoryEntity savedCategoryEntity = categoryRepository.save(categoryEntity1);
		
		categoryRepository.delete(savedCategoryEntity);
		assertEquals(categoryRepository.findById(savedCategoryEntity.getId()), Optional.empty());
	}	
}