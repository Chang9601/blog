package com.whooa.blog.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.comment.service.impl.CommentServiceImpl;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
	@Mock
	private PostRepository postRepository;

	@Mock
	private CommentRepository commentRepository;


	@InjectMocks
	private CommentServiceImpl commentService;

	private PostEntity postEntity;
	private CommentDto.CreateRequest createCommentDto;
	private CommentDto.UpdateRequest updateCommentDto;
	private CommentEntity commentEntity;

	@BeforeEach
	public void setUp() {
		Long id = 1L;
		String name = "홍길동";
		String content = "테스트를 위한 댓글";
		String password = "1234";

		postEntity = new PostEntity(id, "테스트", "테스트를 위한 포스트");
		createCommentDto = new CommentDto.CreateRequest(name, content, password);
		updateCommentDto = new CommentDto.UpdateRequest("실전을 위한 댓글", "1234");
		commentEntity = new CommentEntity(id, name, content, password);
		commentEntity.setPost(postEntity);
	}

	@DisplayName("포스트의 댓글을 생성하는데 성공한다.")
	@Test
	public void givenCreateCommentDto_whenCallCreate_thenReturnCommentDto() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity);

		CommentDto.Response commentDto = commentService.create(postEntity.getId(), createCommentDto);
		
		assertNotNull(commentDto);
		assertEquals(commentDto.getId(), commentEntity.getId());

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
	}

	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		assertThrows(NullPointerException.class, () -> {
			commentService.create(postEntity.getId(), null);	
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).save(any());
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 생성하는데 실패한다.")
	@Test
	public void givenCreateCommentDto_whenCallCreate_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			commentService.create(postEntity.getId(), createCommentDto);	
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
	}	

	@DisplayName("포스트의 댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenReturnAllCommentDtosForPost() {
		CommentEntity commentEntity2 = new CommentEntity(2L, "김철수", "실전을 위한 댓글", "1234");
		commentEntity2.setPost(postEntity);

		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findByPostId(any(Long.class))).willReturn(List.of(commentEntity, commentEntity2));

		List<CommentDto.Response> commentDtos = commentService.findAllByPostId(postEntity.getId());

		assertNotNull(commentDtos);
		assertEquals(commentDtos.size(), 2);

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findByPostId(any(Long.class));
	}

	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글 목록을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenReturnNothing() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findByPostId(any(Long.class))).willReturn(List.of());

		List<CommentDto.Response> commentDtos = commentService.findAllByPostId(postEntity.getId());

		assertNotNull(commentDtos);
		assertEquals(commentDtos.size(), 0);

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findByPostId(any(Long.class));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글 목록을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallFindByPostId_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			commentService.create(postEntity.getId(), createCommentDto);	
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).findByPostId(any(Long.class));
	}	

	@DisplayName("포스트의 댓글을 갱신하는데 성공한다.")
	@Test
	public void givenUpdateCommentDto_whenCallUpdate_thenReturnCommentDto() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity);

		CommentDto.Response commentDto = commentService.update(postEntity.getId(), commentEntity.getId(), updateCommentDto);

		assertNotNull(commentDto);
		assertEquals(commentDto.getContent(), updateCommentDto.getContent());

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).save(any(CommentEntity.class));
	}

	@DisplayName("댓글을 작성하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenNull_whenCallUpdate_thenThrowNullPointerException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));

		assertThrows(NullPointerException.class, () -> {
			commentService.update(postEntity.getId(), commentEntity.getId(), null);	
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenUpdateCommentDto_whenCallUpdate_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			commentService.update(postEntity.getId(), commentEntity.getId(), updateCommentDto);		
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
	}
	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenUpdateCommentDto_whenCallUpdate_thenThrowCommentNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(CommentNotFoundException.class, () -> {
			commentService.update(postEntity.getId(), commentEntity.getId(), updateCommentDto);
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
	}

	@DisplayName("포스트의 댓글이 포스트에 속하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenUpdateCommentDto_whenCallUpdate_thenThrowCommentNotBelongingToPostException() {
		PostEntity postEntity2 = new PostEntity(2L, "테스트", "테스트를 위한 포스트");

		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentService.update(postEntity.getId(), commentEntity.getId(), updateCommentDto);
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).save(any(CommentEntity.class));
	}	

	@DisplayName("포스트의 댓글을 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));
		willDoNothing().given(commentRepository).delete(any(CommentEntity.class));

		commentService.delete(postEntity.getId(), commentEntity.getId());

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).delete(any(CommentEntity.class));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void giventId_whenCallDelete_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			commentService.delete(postEntity.getId(), commentEntity.getId());
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).findById(any(Long.class));
		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
	}

	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowCommentNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(CommentNotFoundException.class, () -> {
			commentService.delete(postEntity.getId(), commentEntity.getId());
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
	}
	
	@DisplayName("포스트의 댓글이 포스트에 속하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowCommentNotBelongingToPostException() {
		PostEntity postEntity2 = new PostEntity(2L, "테스트", "테스트를 위한 포스트");

		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity2));
		given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(commentEntity));

		assertThrows(CommentNotBelongingToPostException.class, () -> {
			commentService.delete(postEntity.getId(), commentEntity.getId());
		});

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(1)).findById(any(Long.class));
		then(commentRepository).should(times(0)).delete(any(CommentEntity.class));
	}
}