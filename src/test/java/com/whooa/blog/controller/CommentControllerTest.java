package com.whooa.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.utils.SerializeDeserializeUtil;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostService postService;
	
	@MockBean
	private CommentService commentService;
	
	@Autowired
	private SerializeDeserializeUtil serializeDeserializeUtil;
	
	private PostEntity postEntity;
	private CommentCreateRequest commentCreate;
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;
	private CommentEntity commentEntity;
	private Long eId;
	private Long dneId;

	@BeforeEach
	public void setUp() {
		Long id = 1L;
		String name = "홍길동";
		String content = "테스트를 위한 댓글";
		String password = "1234";
		
		eId = id;
		dneId = 1000L;
		
		postEntity = new PostEntity(id, "테스트", "테스트를 위한 포스트");
		
		commentEntity = new CommentEntity(id, name, content, password);
		commentEntity.setPost(postEntity);
		
		commentCreate = new CommentCreateRequest(name, content, password);
		commentUpdate = new CommentUpdateRequest("실전을 위한 댓글", "4321");
		comment = new CommentResponse(id, name, content);
	}
	
	@DisplayName("포스트의 댓글을 생성하는데 성공한다.")
	@Test
	public void givenCommentCreate_whenCallCreateComment_thenReturnComment() throws Exception {
		given(commentService.create(any(Long.class), any(CommentCreateRequest.class))).willAnswer((invocation) -> {
			Long postId = invocation.getArgument(0);
			CommentEntity commentEntity =  CommentMapper.INSTANCE.toEntity(invocation.getArgument(1));
			commentEntity.setPost(postEntity);

			return CommentMapper.INSTANCE.toDto(commentEntity);
		});
		
		ResultActions response = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(commentCreate)));

		response.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.name", is(comment.getName())))
				.andExpect(jsonPath("$.data.content", is(comment.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 생성하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallCreateComment_thenThrowPostNotFoundException() throws Exception {
		given(commentService.create(any(Long.class), any(CommentCreateRequest.class))).willThrow(PostNotFoundException.class);
		
		ResultActions response = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(commentCreate)));

		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("포스트의 댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallGetCommentsByPostId_thenReturnAllCommentsForPost() throws Exception {
		CommentEntity commentEntity2 = new CommentEntity(2L, "김철수", "실전을 위한 댓글", "1234");
		commentEntity2.setPost(postEntity);
		
		List<CommentResponse> comments = new ArrayList<>();
		
		comments.add(CommentMapper.INSTANCE.toDto(commentEntity));
		comments.add(CommentMapper.INSTANCE.toDto(commentEntity2));
		
		given(commentService.findAllByPostId(any(Long.class))).willReturn(comments);
		
		ResultActions response = mockMvc.perform(get("/api/v1/posts/{post-id}/comments", eId));

		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.size()", is(comments.size())));
	}
	
	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글 목록을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallGetCommentsByPostId_thenReturnNothing() throws Exception {
		List<CommentResponse> comments = new ArrayList<>();
		
		given(commentService.findAllByPostId(any(Long.class))).willReturn(comments);
		
		ResultActions response = mockMvc.perform(get("/api/v1/posts/{post-id}/comments", eId));

		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.size()", is(comments.size())));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글 목록을 조회하는데 실패한다.")
	@Test
	public void givenPostId_whenCallGetCommentsByPostId_thenThrowPostNotFoundException() throws Exception {		
		given(commentService.findAllByPostId(any(Long.class))).willThrow(PostNotFoundException.class);
		
		ResultActions response = mockMvc.perform(get("/api/v1/posts/{post-id}/comments", dneId));

		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("포스트의 댓글을 갱신하는데 성공한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdateComment_thenReturnComment() throws Exception {
		CommentEntity updatedCommentEntity = new CommentEntity(commentEntity.getId(), commentEntity.getName(), commentUpdate.getContent(), commentUpdate.getPassword());
		CommentResponse updatedCommentDto = new CommentResponse(updatedCommentEntity.getId(), updatedCommentEntity.getName(), updatedCommentEntity.getContent());
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willReturn(updatedCommentDto);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(commentUpdate)));
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.name", is(updatedCommentDto.getName())))
				.andExpect(jsonPath("$.data.content", is(updatedCommentDto.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowPostNotFoundException() throws Exception {
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willThrow(PostNotFoundException.class);
		
		ResultActions response = mockMvc.perform(put("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(commentUpdate)));
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowCommentNotFoundException() throws Exception {
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willThrow(CommentNotFoundException.class);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(commentUpdate)));
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotFoundException));
	}
	
	@DisplayName("포스트의 댓글이 포스트에 속하지 않아서 포스트의 댓글을 갱신하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowCommentNotBelongingToPostException() throws Exception {
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willThrow(CommentNotBelongingToPostException.class);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(commentUpdate)));
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotBelongingToPostException));
	}

	@DisplayName("포스트의 댓글을 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDeleteComment_thenReturnNothing() throws Exception {
		willDoNothing().given(commentService).delete(any(Long.class), any(Long.class));
		
		given(commentService.findAllByPostId(any(Long.class))).willThrow(CommentNotBelongingToPostException.class);

		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId));
		
		response.andDo(print())
				.andExpect(jsonPath("$.metadata.code", is(204)));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteComment_thenThrowPostNotFoundException() throws Exception {
		willThrow(PostNotFoundException.class).given(commentService).delete(any(Long.class), any(Long.class));

		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId));
		
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}	

	@DisplayName("포스트의 댓글이 존재하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteComment_thenThrowCommentNotFoundException() throws Exception {
		willThrow(CommentNotFoundException.class).given(commentService).delete(any(Long.class), any(Long.class));

		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId));
		
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotFoundException));
	}	
	
	@DisplayName("포스트가 존재하지 않아서 포스트의 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteComment_thenThrowCommentNotBelongingToPostException() throws Exception {
		willThrow(CommentNotBelongingToPostException.class).given(commentService).delete(any(Long.class), any(Long.class));

		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{comment-id}", eId, eId));
		
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotBelongingToPostException));
	}
}