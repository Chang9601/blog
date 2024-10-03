package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

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
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private PostRepository postRepository;
	
	@InjectMocks
	private AdminCommentServiceImpl adminCommentServiceImpl;

	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;

	@BeforeAll
	public void setUp() {
		commentUpdate = new CommentUpdateRequest().content("댓글2");
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenReturnNothing(CommentEntity commentEntity, PostEntity postEntity, UserEntity userEntity) {
		willDoNothing().given(commentRepository).delete(any(CommentEntity.class));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		adminCommentServiceImpl.delete(commentEntity.getId(), postEntity.getId());

		then(commentRepository).should(times(1)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenId_whenCallDelete_thenThrowPostNotFoundException(CommentEntity commentEntity, PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			adminCommentServiceImpl.delete(commentEntity.getId(), postEntity.getId());
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
			adminCommentServiceImpl.delete(commentEntity.getId(), postEntity.getId());
		});

		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("commentParametersProvider")
	public void givenCommentUpdate_whenCallUpdate_thenReturnComment(CommentEntity commentEntity1, PostEntity postEntity, UserEntity userEntity) {
		CommentEntity commentEntity2;
		
		commentEntity2 = CommentEntity.builder()
							.content(commentUpdate.getContent())
							.parentId(null)
							.post(postEntity)
							.user(userEntity)
							.build();
		
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity2);
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity1));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		comment = adminCommentServiceImpl.update(commentEntity1.getId(), postEntity.getId(), commentUpdate);

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
			adminCommentServiceImpl.update(commentEntity.getId(), postEntity.getId(), commentUpdate);
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
			adminCommentServiceImpl.update(commentEntity.getId(), postEntity.getId(), commentUpdate);
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
		
		postEntity2 = PostEntity.builder()
						.id(2L)
						.content(postEntity1.getContent())
						.title(postEntity1.getTitle())
						.category(categoryEntity)
						.user(userEntity)
						.build();
		
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			adminCommentServiceImpl.update(commentEntity.getId(), postEntity1.getId(), commentUpdate);
		});
		
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	private static Stream<Arguments> commentParametersProvider() {
		CategoryEntity categoryEntity;
		CommentEntity commentEntity;
		UserEntity userEntity;
		PostEntity postEntity;
		
		categoryEntity = CategoryEntity.builder()
							.name("카테고리1")
							.build();
		
		userEntity = UserEntity.builder()
						.id(1L)
						.email("user1@naver.com")
						.name("사용자")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build();
					
		postEntity = PostEntity.builder()
						.content("포스트1")
						.title("포스트1")
						.category(categoryEntity)
						.user(userEntity)
						.build();

		commentEntity = CommentEntity.builder()
							.content("댓글1")
							.parentId(null)
							.post(postEntity)
							.user(userEntity)
							.build();

		return Stream.of(Arguments.of(commentEntity, postEntity, userEntity, categoryEntity));
	}		
}