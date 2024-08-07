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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDTO.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDTO.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDTO.CommentResponse;
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
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private CommentServiceImpl commentServiceImpl;

	private CommentEntity commentEntity1;
	private CategoryEntity categoryEntity;
	private PostEntity postEntity1;
	private UserEntity userEntity1;

	private CommentCreateRequest commentCreate;
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;
	
	private PaginationUtil pagination;
	
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	public void setUpAll() {
		categoryEntity = new CategoryEntity().name("테스트 카테고리");
		
		userEntity1 = new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER);
		
		postEntity1 = new PostEntity()
				.content("테스트 내용")
				.title("테스트 제목")
				.category(categoryEntity)
				.user(userEntity1);
		
		pagination = new PaginationUtil();
		
		userDetailsImpl = new UserDetailsImpl(userEntity1);
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities()));
		SecurityContextHolder.setContext(securityContext);
	}
	
	@BeforeEach
	public void setUpEach() {
		String content = "테스트 내용";

		commentEntity1 = new CommentEntity()
				.content(content)
				.parentId(null)
				.post(postEntity1)
				.user(userEntity1);
		
		commentCreate = new CommentCreateRequest().content(content);
		commentUpdate = new CommentUpdateRequest().content("실전 내용");
	}

	@DisplayName("댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentCreate_whenCallCreate_thenReturnComment() {
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity1);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));

		comment = commentServiceImpl.create(postEntity1.getId(), commentCreate, userDetailsImpl);
		
		assertEquals(comment.getContent(), commentEntity1.getContent());

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
		then(userRepository).should(times(0)).findById(any(Long.class));
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
		then(userRepository).should(times(0)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아서 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowCommentNotBelongingToPostException() {
		PostEntity postEntity2 = new PostEntity()
				.content(postEntity1.getContent())
				.title(postEntity1.getTitle())
				.category(categoryEntity)
				.user(userEntity1);
		postEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), userDetailsImpl);
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		

	@DisplayName("로그인한 사용자와 댓글을 작성한 사용자가 일치하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowUserNotMatchedException() {
		UserEntity userEntity2 = new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER);
		userEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		assertThrows(UserNotMatchedException.class, () -> {
			commentServiceImpl.delete(commentEntity1.getId(), postEntity1.getId(), userDetailsImpl);
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		

	@DisplayName("댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallFindAllByPostId_thenReturnAllCommentsForPost() {
		CommentEntity commentEntity2 = new CommentEntity()
				.content("실전 내용")
				.parentId(null)
				.post(postEntity1)
				.user(userEntity1);

		given(commentRepository.findByPostId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<CommentEntity>(List.of(commentEntity1, commentEntity2)));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));


		PageResponse<CommentResponse> page = commentServiceImpl.findAllByPostId(postEntity1.getId(), pagination);

		assertEquals(page.getTotalElements(), 2);

		then(commentRepository).should(times(1)).findByPostId(any(Long.class), any(Pageable.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("댓글에 답하는데 성공한다.")
	@Test
	public void givenCommentCreate_whenCallReply_thenReturnComment() {
		CommentEntity commentEntity2 = new CommentEntity()
				.content("실전 내용")
				.parentId(commentEntity1.getId())
				.post(postEntity1)
				.user(userEntity1);
		
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		comment = commentServiceImpl.reply(commentEntity1.getId(), postEntity1.getId(), commentCreate, userDetailsImpl); 
		
		assertEquals(comment.getParentId(), commentEntity1.getId());

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
		PostEntity postEntity2 = new PostEntity()
				.content(postEntity1.getContent())
				.title(postEntity1.getTitle())
				.category(categoryEntity)
				.user(userEntity1);
		postEntity2.setId(2L);
		
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
		CommentEntity commentEntity2 = new CommentEntity()
				.content(commentUpdate.getContent())
				.parentId(null)
				.post(postEntity1)
				.user(userEntity1);		
		
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		comment = commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);

		assertEquals(comment.getContent(), commentUpdate.getContent());

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
		PostEntity postEntity2 = new PostEntity()
				.content(postEntity1.getContent())
				.title(postEntity1.getTitle())
				.category(categoryEntity)
				.user(userEntity1);
		postEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("로그인한 사용자와 댓글을 작성한 사용자가 일치하지 않아 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdate_thenThrowUserNotMatchedException() {
		UserEntity userEntity2 = new UserEntity()
				.email("real@real.com")
				.name("실전 이름")
				.password("1234")
				.userRole(UserRole.USER);
		userEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		assertThrows(UserNotMatchedException.class, () -> {
			commentServiceImpl.update(commentEntity1.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
}