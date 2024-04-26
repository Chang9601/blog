package com.whooa.blog.repository;

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

import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CommentRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	private PostEntity postEntity;
	private CommentEntity commentEntity;

	@BeforeEach
	public void setUp() {
		postEntity = postRepository.save(new PostEntity("테스트", "테스트를 위한 포스트"));
		commentEntity = new CommentEntity("홍길동", "테스트를 위한 댓글", "1234");
		commentEntity.setPost(postEntity);
	}
	
	@DisplayName("포스트의 댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSave_thenReturnCommentEntity() {		
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
		
		assertNotNull(savedCommentEntity);
		assertTrue(savedCommentEntity.getId() > 0);
	}

	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSave_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.save(null);
		});
	}
	
	@DisplayName("포스트의 댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostEntityId_whenCallFindByPostId_thenReturnAllCommentEntitiesForPostEntity() {
		CommentEntity commentEntity2 = new CommentEntity("김철수", "실전을 위한 댓글", "4321");
		commentEntity2.setPost(postEntity);

		postRepository.save(postEntity);
		
		List<CommentEntity> commentEntities = commentRepository.findByPostId(postEntity.getId());
				
		assertEquals(commentEntities.size(), 2);			
	}
	
//	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글 목록을 조회하는데 실패한다.")
//	@Test
//	public void givenPostEntityId_whenCallFindByPostId_thenReturnNothing() {
//		List<CommentEntity> commentEntities = commentRepository.findByPostId(postEntity.getId());
//		
//		assertEquals(commentEntities.size(), 0);
//	}
	
	@DisplayName("포스트의 댓글을 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindById_thenReturnCommentEntity() {		
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();

		assertNotNull(foundCommentEntity);
		assertEquals(foundCommentEntity.getName(), savedCommentEntity.getName());		
	}
	
	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글을 조회하는데 실패한다")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {
		commentRepository.save(commentEntity);
		
		assertThrows(NoSuchElementException.class, () -> {
			commentRepository.findById(1000L).get();
		});
	}
	
	@DisplayName("포스트의 댓글을 갱신하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSave_thenReturnUpdatedCommentEntity() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();
		
		foundCommentEntity.setContent("실전을 위한 댓글");
		
		CommentEntity updatedCommentEntity = commentRepository.save(foundCommentEntity);

		assertEquals(updatedCommentEntity.getName(), foundCommentEntity.getName());
	}
	
	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenNull_whenCallSave_thenInvalidDataAccessApiUsageException() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();
		
		foundCommentEntity.setContent("실전을 위한 댓글");

		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.save(null);
		});
	}
	
	@DisplayName("포스트의 댓글을 삭제하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallDelete_thenReturnNothing() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity);

		commentRepository.delete(savedCommentEntity);
		
		assertEquals(commentRepository.findById(savedCommentEntity.getId()), Optional.empty());
	}
	
	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {		
		commentRepository.save(commentEntity);
		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.delete(null);
		});
	}
}