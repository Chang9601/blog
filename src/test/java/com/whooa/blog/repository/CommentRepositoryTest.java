package com.whooa.blog.repository;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

import static org.junit.jupiter.api.Assertions.*;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentRepositoryTest {
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	
	private CommentEntity commentEntity1;
	private CategoryEntity categoryEntity;
	private PostEntity postEntity;
	private UserEntity userEntity;
	
	@BeforeAll
	public void setUpAll() {
		categoryEntity = categoryRepository.save(
			CategoryEntity.builder()
				.name("카테고리")
				.build()
		);

		userEntity = userRepository.save(
			UserEntity.builder()
				.email("user@naver.com")
				.name("사용자")
				.password("12345678Aa!@#$%")
				.userRole(UserRole.USER)
				.build()
		);
		
		postEntity = postRepository.save(
			PostEntity.builder()
				.content("포스트")
				.title("포스트")
				.category(categoryEntity)
				.user(userEntity)
				.build()
		);
	}
	
	@BeforeEach
	public void setUpEach() {
		commentEntity1 = CommentEntity.builder()
							.content("댓글1")	
							.post(postEntity)
							.user(userEntity)
							.build();
	}
	
	@AfterAll
	public void tearDown() {
		postRepository.deleteAll();
		categoryRepository.deleteAll();
		commentRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@DisplayName("댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSaveForCreate_thenReturnCommentEntity() {		
		CommentEntity savedCommentEntity;
		
		savedCommentEntity = commentRepository.save(commentEntity1);
		
		assertTrue(savedCommentEntity.getId() > 0);
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 생성하는데 실패한다.")
	@Test
	public void givenPostId_whenCallSaveForCreate_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(100L).get();
		});
	}
	
	@DisplayName("댓글을 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForCreate_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.save(null);
		});
	}

	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallDelete_thenReturnNothing(CommentEntity commentEntity1) {
		CommentEntity savedCommentEntity;
		
		savedCommentEntity = commentRepository.save(commentEntity1);

		commentRepository.delete(savedCommentEntity);
		
		assertEquals(Optional.empty(), commentRepository.findById(savedCommentEntity.getId()));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 생성하는데 실패한다.")
	@Test
	public void givenPostId_whenCallDelete_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(100L).get();
		});
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.save(null);
		});
	}
	
	@DisplayName("댓글을 조회하는데 성공한다.")
	public void givenId_whenCallFindById_thenReturnCommentEntity() {		
		CommentEntity foundCommentEntity, savedCommentEntity;
		
		savedCommentEntity = commentRepository.save(commentEntity1);
		foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();

		assertEquals(savedCommentEntity.getContent(), foundCommentEntity.getContent());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallFindById_ThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(100L).get();
		});
	}
	
	@DisplayName("댓글이 존재하지 않아 조회하는데 실패한다")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {		
		assertThrows(NoSuchElementException.class, () -> {
			commentRepository.findById(100L).get();
		});
	}
	
	@DisplayName("댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenReturnCommentEntitiesForPostEntity() {
		CommentEntity commentEntity2;
		Page<CommentEntity> page;
		
		commentEntity2 = CommentEntity.builder()
							.content("댓글2")
							.post(postEntity)
							.user(userEntity)
							.build();
		
		commentRepository.save(commentEntity1);
		commentRepository.save(commentEntity2);
		
		page = commentRepository.findByPostId(postEntity.getId(), new PaginationUtil().makePageable());
				
		assertEquals(2, page.getTotalElements());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글 목록을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(100L).get();
		});
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSaveForUpdate_thenReturnUpdatedCommentEntity() {
		CommentEntity foundCommentEntity, savedCommentEntity, updatedCommentEntity;
		
		savedCommentEntity = commentRepository.save(commentEntity1);
		foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();
		
		foundCommentEntity.setContent("실전 내용");
		
		updatedCommentEntity = commentRepository.save(foundCommentEntity);
		
		assertEquals(foundCommentEntity.getContent(), updatedCommentEntity.getContent());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	public void givenPostId_whenCallSaveForUpdate_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(100L).get();
		});
	}	
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForUpdate_thenThrowInvalidDataAccessApiUsageException() {
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.save(null);
		});
	}
}