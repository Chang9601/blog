package com.whooa.blog.admin.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.whooa.blog.admin.service.impl.AdminCommentServiceImpl;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminCommentServiceTest {
	@InjectMocks
	private AdminCommentServiceImpl adminCommentServiceImpl;
	
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private PostRepository postRepository;

	private CommentEntity commentEntity1;
	private CategoryEntity categoryEntity1;
	private UserEntity userEntity;
	private PostEntity postEntity1;
	
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;

	@BeforeAll
	public void setUp() {
		commentUpdate = new CommentUpdateRequest();
		commentUpdate.setContent("댓글2");
		
		categoryEntity1 = new CategoryEntity();
		categoryEntity1.setName("카테고리1");

		userEntity = new UserEntity();
		userEntity.setEmail("user1@naver.com");
		userEntity.setName("사용자");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);

		postEntity1 = new PostEntity();
		postEntity1.setId(1L);
		postEntity1.setContent("포스트1");
		postEntity1.setTitle("포스트1");
		postEntity1.setCategory(categoryEntity1);
		postEntity1.setUser(userEntity);

		commentEntity1 = new CommentEntity();
		commentEntity1.setContent("댓글1");
		commentEntity1.setParentId(null);
		commentEntity1.setPost(postEntity1);
		commentEntity1.setUser(userEntity);
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		willDoNothing().given(commentRepository).delete(any(CommentEntity.class));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		adminCommentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId());

		then(commentRepository).should(times(1)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			adminCommentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId());
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowCommentNotFoundException() {
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		
		assertThrows(CommentNotFoundException.class, () -> {
			adminCommentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId());
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenReturnComment() {
		CommentEntity commentEntity2;
		
		commentEntity2 = new CommentEntity();
		commentEntity2.setContent(commentUpdate.getContent());
		commentEntity2.setParentId(null);
		commentEntity2.setPost(postEntity1);
		commentEntity2.setUser(userEntity);
	
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		comment = adminCommentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate);

		assertEquals(commentUpdate.getContent(), comment.getContent());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenThrowPostNotFoundException() {	
		assertThrows(PostNotFoundException.class, () -> {
			adminCommentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate);
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenThrowCommentNotFoundException() {	
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		assertThrows(CommentNotFoundException.class, () -> {
			adminCommentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate);
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenThrowCommentNotBelongingToPostException() {
		PostEntity postEntity2;

		postEntity2 = new PostEntity();
		postEntity2.setId(2L);
		postEntity2.setContent("포스트2");
		postEntity2.setTitle("포스트2");
		postEntity2.setCategory(categoryEntity1);
		postEntity2.setUser(userEntity);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			adminCommentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate);
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		
}