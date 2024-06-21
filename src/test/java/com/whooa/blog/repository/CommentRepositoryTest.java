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
	private CommentEntity commentEntity1;

	@BeforeEach
	public void setUp() {
		postEntity = postRepository.save(new PostEntity().content("테스트 내용").title("테스트 제목").category(null));
		commentEntity1 = new CommentEntity().content("테스트 댓글").name("홍길동").password("1234");
		commentEntity1.setPost(postEntity);
	}
	
	@DisplayName("포스트의 댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSave_thenReturnCommentEntity() {		
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		
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
		CommentEntity commentEntity2 = new CommentEntity().content("테스트 댓글").name("김철수").password("1579");
		commentEntity2.setPost(postEntity);

		postRepository.save(postEntity);
		
		Page<CommentEntity> commentEntities = commentRepository.findByPostId(postEntity.getId(), null);
				
		assertEquals(commentEntities.getTotalElements(), 2);			
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
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();

		assertNotNull(foundCommentEntity);
		assertEquals(foundCommentEntity.getName(), savedCommentEntity.getName());		
	}
	
	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글을 조회하는데 실패한다")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {
		commentRepository.save(commentEntity1);
		
		assertThrows(NoSuchElementException.class, () -> {
			commentRepository.findById(1000L).get();
		});
	}
	
	@DisplayName("포스트의 댓글을 갱신하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallSave_thenReturnUpdatedCommentEntity() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();
		
		foundCommentEntity.setContent("실전을 위한 댓글");
		
		CommentEntity updatedCommentEntity = commentRepository.save(foundCommentEntity);

		assertEquals(updatedCommentEntity.getName(), foundCommentEntity.getName());
	}
	
	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenNull_whenCallSave_thenInvalidDataAccessApiUsageException() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);
		CommentEntity foundCommentEntity = commentRepository.findById(savedCommentEntity.getId()).get();
		
		foundCommentEntity.setContent("실전을 위한 댓글");

		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.save(null);
		});
	}
	
	@DisplayName("포스트의 댓글을 삭제하는데 성공한다.")
	@Test
	public void givenCommentEntity_whenCallDelete_thenReturnNothing() {
		CommentEntity savedCommentEntity = commentRepository.save(commentEntity1);

		commentRepository.delete(savedCommentEntity);
		
		assertEquals(commentRepository.findById(savedCommentEntity.getId()), Optional.empty());
	}
	
	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {		
		commentRepository.save(commentEntity1);
		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			commentRepository.delete(null);
		});
	}
}