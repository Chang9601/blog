package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private CommentServiceImpl commentServiceImpl;

	private CommentCreateRequest commentCreate;
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;
	
	private PaginationUtil pagination;
	
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	public void setUp() {
		commentCreate = new CommentCreateRequest().content("댓글1");
		commentUpdate = new CommentUpdateRequest().content("댓글2");
		
		pagination = new PaginationUtil();
		
		UserEntity userEntity = new UserEntity()
				.email("user1@naver.com")
				.name("사용자1")
				.password("12345678Aa!@#$%")
				.userRole(UserRole.USER);
		userEntity.setId(1L);
		
		userDetailsImpl = new UserDetailsImpl(userEntity);
	}

	@DisplayName("댓글을 생성하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentCreate_whenCallCreate_thenReturnComment(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity) {
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));

		comment = commentServiceImpl.create(postEntity.getId(), commentCreate, userDetailsImpl);
		
		assertEquals(commentEntity.getContent(), comment.getContent());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아서 댓글을 생성하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentCreate_whenCallCreate_thenThrowPostNotFoundException(CommentEntity commentEntity, PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.create(postEntity.getId(), commentCreate, userDetailsImpl);	
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 생성하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenNull_whenCallCreate_thenThrowNullPointerException(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));

		assertThrows(NullPointerException.class, () -> {
			commentServiceImpl.create(postEntity.getId(), null, userDetailsImpl);
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenReturnNothing(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity) {
		willDoNothing().given(commentRepository).delete(any(CommentEntity.class));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		commentServiceImpl.delete(commentEntity.getId(), postEntity.getId(), userDetailsImpl);

		then(commentRepository).should(times(1)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenThrowPostNotFoundException(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.delete(commentEntity.getId(), postEntity.getId(), userDetailsImpl);
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenThrowCommentNotFoundException(CommentEntity commentEntity, PostEntity postEntity) {
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		
		assertThrows(CommentNotFoundException.class, () -> {
			commentServiceImpl.delete(commentEntity.getId(), postEntity.getId(), userDetailsImpl);
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아서 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenThrowCommentNotBelongingToPostException(CommentEntity commentEntity, PostEntity postEntity1, UserEntity userEntity, CategoryEntity categoryEntity) {
		PostEntity postEntity2;
		
		postEntity2 = new PostEntity()
					.content(postEntity1.getContent())
					.title(postEntity1.getTitle())
					.category(categoryEntity)
					.user(userEntity);
		postEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.delete(commentEntity.getId(), postEntity1.getId(), userDetailsImpl);
		});	

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		

	@DisplayName("댓글을 작성한 사용자와 일치하지 않아 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenThrowUserNotMatchedException(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity1) {
		UserEntity userEntity2;
		
		userEntity2 = new UserEntity()
					.email("user2@naver.com")
					.name("사용자2")
					.password("12345678Aa!@#$%")
					.userRole(UserRole.USER);
		userEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		assertThrows(UserNotMatchedException.class, () -> {
			commentServiceImpl.delete(commentEntity.getId(), postEntity.getId(), new UserDetailsImpl(userEntity2));
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}		

	@DisplayName("댓글 목록을 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenPostId_whenCallFindAllByPostId_thenReturnAllCommentsForPost(CommentEntity commentEntity1, PostEntity postEntity, UserEntity userEntity) {
		CommentEntity commentEntity2;
		PageResponse<CommentResponse> page;
		
		commentEntity2 = new CommentEntity()
						.content("댓글2")
						.parentId(null)
						.post(postEntity)
						.user(userEntity);

		given(commentRepository.findByPostId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<CommentEntity>(List.of(commentEntity1, commentEntity2)));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		page = commentServiceImpl.findAllByPostId(postEntity.getId(), pagination);

		assertEquals(2, page.getTotalElements());

		then(commentRepository).should(times(1)).findByPostId(any(Long.class), any(Pageable.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("댓글에 답하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentCreate_whenCallReply_thenReturnComment(CommentEntity commentEntity1, PostEntity postEntity, UserEntity userEntity) {
		CommentEntity commentEntity2;
		
		commentEntity2 = new CommentEntity()
						.content("댓글2")
						.parentId(commentEntity1.getId())
						.post(postEntity)
						.user(userEntity);

		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		
		comment = commentServiceImpl.reply(commentEntity1.getId(), postEntity.getId(), commentCreate, userDetailsImpl); 
		
		assertEquals(commentEntity1.getId(), comment.getParentId());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("포스트가 존재하지 않아 답하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentCreate_whenCallReply_thenThrowPostNotFoundException(CommentEntity commentEntity1, PostEntity postEntity, UserEntity userEntity) {		
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.reply(commentEntity1.getId(), postEntity.getId(), commentCreate, userDetailsImpl); 
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 존재하지 않아 답하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentCreate_whenCallReply_thenThrowCommentNotFoundException(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity) {		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		
		assertThrows(CommentNotFoundException.class, () -> {
			commentServiceImpl.reply(commentEntity.getId(), postEntity.getId(), commentCreate, userDetailsImpl); 
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아 답하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentCreate_whenCallReply_thenThrowCommentNotBelongingToPostException(CommentEntity commentEntity, PostEntity postEntity1, UserEntity userEntity, CategoryEntity categoryEntity) {
		PostEntity postEntity2;
		
		postEntity2 = new PostEntity()
					.content(postEntity1.getContent())
					.title(postEntity1.getTitle())
					.category(categoryEntity)
					.user(userEntity);
		postEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		
		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.reply(commentEntity.getId(), postEntity1.getId(), commentCreate, userDetailsImpl); 
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentUpdate_whenCallUpdate_thenReturnComment(CommentEntity commentEntity1, PostEntity postEntity, UserEntity userEntity, CategoryEntity categoryEntity) {
		CommentEntity commentEntity2;
		
		commentEntity2 = new CommentEntity()
						.content(commentUpdate.getContent())
						.parentId(null)
						.post(postEntity)
						.user(userEntity);		
		
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		comment = commentServiceImpl.update(commentEntity1.getId(), postEntity.getId(), commentUpdate, userDetailsImpl);

		assertEquals(commentUpdate.getContent(), comment.getContent());

		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentUpdate_whenCallUpdate_thenThrowPostNotFoundException(CommentEntity commentEntity, PostEntity postEntity) {	
		assertThrows(PostNotFoundException.class, () -> {
			commentServiceImpl.update(commentEntity.getId(), postEntity.getId(), commentUpdate, userDetailsImpl);
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentUpdate_whenCallUpdate_thenThrowCommentNotFoundException(CommentEntity commentEntity, PostEntity postEntity) {	
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		assertThrows(CommentNotFoundException.class, () -> {
			commentServiceImpl.update(commentEntity.getId(), postEntity.getId(), commentUpdate, userDetailsImpl);
		});

		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글이 포스트에 속하지 않아 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentUpdate_whenCallUpdate_thenThrowCommentNotBelongingToPostException(CommentEntity commentEntity, PostEntity postEntity1, UserEntity userEntity, CategoryEntity categoryEntity) {
		PostEntity postEntity2;
		
		postEntity2 = new PostEntity()
					.content(postEntity1.getContent())
					.title(postEntity1.getTitle())
					.category(categoryEntity)
					.user(userEntity);
		postEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentServiceImpl.update(commentEntity.getId(), postEntity1.getId(), commentUpdate, userDetailsImpl);
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 작성한 사용자와 일치하지 않아 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentUpdate_whenCallUpdate_thenThrowUserNotMatchedException(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity1) {
		UserEntity userEntity2;
		
		userEntity2 = new UserEntity()
					.email("user2@naver.com")
					.name("사용자2")
					.password("12345678Aa!@#$%")
					.userRole(UserRole.USER);
		userEntity2.setId(2L);
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		assertThrows(UserNotMatchedException.class, () -> {
			commentServiceImpl.update(commentEntity.getId(), postEntity.getId(), commentUpdate, new UserDetailsImpl(userEntity2));
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	private static Stream<Arguments> commentParametersProvider() {
		CategoryEntity categoryEntity = new CategoryEntity().name("카테고리1");
		
		UserEntity userEntity = new UserEntity()
								.email("user1@naver.com")
								.name("사용자1")
								.password("12345678Aa!@#$%")
								.userRole(UserRole.USER);
		userEntity.setId(1L);
			
		PostEntity postEntity = new PostEntity()
								.content("포스트1")
								.title("포스트1")
								.category(categoryEntity)
								.user(userEntity);

		CommentEntity commentEntity = new CommentEntity()
									.content("댓글1")
									.parentId(null)
									.post(postEntity)
									.user(userEntity);

		return Stream.of(Arguments.of(commentEntity, postEntity, userEntity, categoryEntity));
	}		
}