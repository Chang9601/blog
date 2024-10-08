package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
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

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.comment.service.impl.CommentServiceImpl;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentServiceTest {
	@InjectMocks
	private CommentServiceImpl commentServiceImpl;
	
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;
	
	private CommentEntity commentEntity1;
	private CategoryEntity categoryEntity;
	private PostEntity postEntity1;
	private UserEntity userEntity1;
	
	private CommentCreateRequest commentCreate;
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;
	
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	public void setUpAll() {
		commentCreate = new CommentCreateRequest();
		commentCreate.setContent("댓글1");

		commentUpdate = new CommentUpdateRequest();
		commentUpdate.setContent("댓글2");

		categoryEntity = new CategoryEntity();
		categoryEntity.setName("카테고리1");

		userEntity1 = new UserEntity();
		userEntity1.setId(1L);
		userEntity1.setEmail("user1@naver.com");
		userEntity1.setName("사용자1");
		userEntity1.setPassword("12345678Aa!@#$%");
		userEntity1.setUserRole(UserRole.USER);

		postEntity1 = new PostEntity();
		postEntity1.setId(1L);
		postEntity1.setContent("포스트1");
		postEntity1.setTitle("포스트1");
		postEntity1.setCategory(categoryEntity);
		postEntity1.setUser(userEntity1);
				
		userDetailsImpl = new UserDetailsImpl(userEntity1);
	}
	
	@BeforeEach
	public void setUpEach() {
		commentEntity1 = new CommentEntity();
		commentEntity1.setContent("댓글1");
		commentEntity1.setPost(postEntity1);
		commentEntity1.setUser(userEntity1);
	}

	@DisplayName("댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentCreate_whenCallCreate_thenReturnComment() {		
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity1);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));

		comment = commentServiceImpl.create(postEntity1.getId(), commentCreate, userDetailsImpl);
		
		assertEquals(commentEntity1.getContent(), comment.getContent());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아서 댓글을 생성하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallCreate_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.create(postEntity1.getId(), commentCreate, userDetailsImpl);	
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));

		assertThrows(NullPointerException.class, () -> {
			commentServiceImpl.create(postEntity1.getId(), null, userDetailsImpl);
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		willDoNothing().given(commentRepository).delete(any(CommentEntity.class));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), userDetailsImpl);

		then(commentRepository).should(times(1)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), userDetailsImpl);
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
			commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), userDetailsImpl);
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아서 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowCommentNotBelongingToPostException() {
		PostEntity postEntity2;
		
		postEntity2 = new PostEntity();
		postEntity2.setId(2L);
		postEntity2.setContent("포스트2");
		postEntity2.setTitle("포스트2");
		postEntity2.setCategory(categoryEntity);
		postEntity2.setUser(userEntity1);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), userDetailsImpl);
		});	

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		

	@DisplayName("댓글을 작성한 사용자와 일치하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowUserNotMatchedException() {
		UserEntity userEntity2;
		
		userEntity2 = new UserEntity();
		userEntity2.setId(2L);
		userEntity2.setEmail("user2@naver.com");
		userEntity2.setName("사용자2");
		userEntity2.setPassword("12345678Aa!@#$%");
		userEntity2.setUserRole(UserRole.USER);
	
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		assertThrows(UserNotMatchedException.class, () -> {
			commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), new UserDetailsImpl(userEntity2));
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		

	@DisplayName("댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallFindAllByPostId_thenReturnAllCommentsForPost() {
		CommentEntity commentEntity2;
		PageResponse<CommentResponse> page;
		
		commentEntity2 = new CommentEntity();
		commentEntity2.setContent("댓글2");
		commentEntity2.setPost(postEntity1);
		commentEntity2.setUser(userEntity1);

		given(commentRepository.findByPostId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<CommentEntity>(List.of(commentEntity1, commentEntity2)));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		page = commentServiceImpl.findAllByPostId(postEntity1.getId(), new PaginationUtil());

		assertEquals(2, page.getTotalElements());

		then(commentRepository).should(times(1)).findByPostId(any(Long.class), any(Pageable.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("댓글에 답하는데 성공한다.")
	@Test
	public void givenCommentCreate_whenCallReply_thenReturnComment() {
		CommentEntity commentEntity2;
		
		commentEntity2 = new CommentEntity();
		commentEntity2.setContent("댓글2");
		commentEntity2.setParentId(commentEntity1.getId());
		commentEntity2.setPost(postEntity1);
		commentEntity2.setUser(userEntity1);
		
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		comment = commentServiceImpl.reply(commentEntity1.getId(), postEntity1.getId(), commentCreate, userDetailsImpl); 
		
		assertEquals(commentEntity2.getParentId(), comment.getId());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("포스트가 존재하지 않아 답하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallReply_thenThrowPostNotFoundException() {		
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.reply(commentEntity1.getId(), postEntity1.getId(), commentCreate, userDetailsImpl); 
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 존재하지 않아 답하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallReply_thenThrowCommentNotFoundException() {		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		assertThrows(CommentNotFoundException.class, () -> {
			commentServiceImpl.reply(commentEntity1.getId(), postEntity1.getId(), commentCreate, userDetailsImpl); 
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아 답하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallReply_thenThrowCommentNotBelongingToPostException() {
		PostEntity postEntity2;
		
		postEntity2 = new PostEntity();
		postEntity2.setId(2L);
		postEntity2.setContent("포스트2");
		postEntity2.setTitle("포스트2");
		postEntity2.setCategory(categoryEntity);
		postEntity2.setUser(userEntity1);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.reply(commentEntity1.getId(), postEntity1.getId(), commentCreate, userDetailsImpl); 
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenReturnComment() {
		CommentEntity commentEntity2;
		
		commentEntity2 = new CommentEntity();
		commentEntity2.setContent(commentUpdate.getContent());
		commentEntity2.setPost(postEntity1);
		commentEntity2.setUser(userEntity1);
				
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		comment = commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);

		assertEquals(commentEntity2.getContent(), comment.getContent());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenThrowPostNotFoundException() {	
		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);
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
			commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);
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
		postEntity2.setCategory(categoryEntity);
		postEntity2.setUser(userEntity1);
				
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 작성한 사용자와 일치하지 않아 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenThrowUserNotMatchedException() {
		UserEntity userEntity2;
		
		userEntity2 = new UserEntity();
		userEntity2.setId(2L);
		userEntity2.setEmail("user2@naver.com");
		userEntity2.setName("사용자2");
		userEntity2.setPassword("12345678Aa!@#$%");
		userEntity2.setUserRole(UserRole.USER);

		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		assertThrows(UserNotMatchedException.class, () -> {
			commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, new UserDetailsImpl(userEntity2));
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}	
}