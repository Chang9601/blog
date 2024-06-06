package com.whooa.blog.repository;

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

	private PaginationUtil pagination;
	private Pageable pageable;

	@BeforeEach
	public void setUp() {
		categoryEntity = categoryRepository.save(new CategoryEntity().name("테스트 카테고리"));

		userEntity = userRepository.save(new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER));
		
		postEntity = postRepository.save(new PostEntity()
				.content("테스트 내용")
				.title("테스트 제목")
				.category(categoryEntity)
				.user(userEntity));
		
		commentEntity1 = new CommentEntity()
				.content("테스트 내용")
				.post(postEntity)
				.user(userEntity);
		
		pagination = new PaginationUtil();
		pageable = pagination.makePageable();
	}
	
	@DisplayName("댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSaveForCreate_thenReturnCommentEntity() {		
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		
		assertTrue(savedCommentEntity.getId() > 0);
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 생성하는데 실패한다.")
	@Test
	public void givenPostId_whenCallSaveForCreate_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(2L).get();
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
	public void givenCommentEntity_whenCallDelete_thenReturnNothing() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);

		commentRepository.delete(savedCommentEntity);
		
		assertEquals(commentRepository.findById(savedCommentEntity.getId()), Optional.empty());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 생성하는데 실패한다.")
	@Test
	public void givenPostId_whenCallDelete_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(2L).get();
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
	@Test
	public void givenId_whenCallFindById_thenReturnCommentEntity() {		
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();

		assertEquals(foundCommentEntity.getContent(), savedCommentEntity.getContent());		
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallFindById_ThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(2L).get();
		});
	}
	
	
	@DisplayName("댓글이 존재하지 않아 조회하는데 실패한다")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {		
		assertThrows(NoSuchElementException.class, () -> {
			commentRepository.findById(1L).get();
		});
	}
	
	@DisplayName("댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenReturnCommentEntitiesForPostEntity() {
		CommentEntity commentEntity2 = new CommentEntity()
				.content("테스트 내용")
				.post(postEntity)
				.user(userEntity);
		
		commentRepository.save(commentEntity1);
		commentRepository.save(commentEntity2);
		
		Page<CommentEntity> page = commentRepository.findByPostId(postEntity.getId(), pageable);
				
		assertEquals(page.getTotalElements(), 2);			
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글 목록을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(2L).get();
		});
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSaveForUpdate_thenReturnUpdatedCommentEntity() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();
		
		foundCommentEntity.content("실전 내용");
		
		CommentEntity updatedCommentEntity = commentRepository.save(foundCommentEntity);
		
		assertEquals(updatedCommentEntity.getContent(), foundCommentEntity.getContent());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	public void givenPostId_whenCallSaveForUpdate_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(2L).get();
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