package com.whooa.blog.controller;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.controller.CommentController;
import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

@WebMvcTest(controllers = {CommentController.class})
@ContextConfiguration(classes = {CommentController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AllExceptionHandler.class)
public class CommentControllerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@MockBean
	private CommentService commentService;
	
	private CommentEntity commentEntity;
	private CategoryEntity categoryEntity;
	private PostEntity postEntity;
	private UserEntity userEntity;

	private CommentCreateRequest commentCreate;
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment1;

	@BeforeAll
	public void setUpAll() {
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.addFilter(new CharacterEncodingFilter("utf-8", true))
					.apply(springSecurity()).build();
		
		userEntity = UserEntity.builder()
						.email("user1@naver.com")
						.name("사용자1")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build();

		categoryEntity = CategoryEntity.builder()
							.name("카테고리")
							.build();

		postEntity = PostEntity.builder()
						.content("포스트")
						.title("포스트")
						.category(categoryEntity)
						.user(userEntity)
						.build();
	}
	
	@BeforeEach
	public void setUpEach() {
		String content;
		
		content = "댓글1";
		
		commentEntity = CommentEntity.builder()
							.content(content)
							.parentId(null)
							.post(postEntity)
							.user(userEntity)
							.build();
		
		commentCreate = new CommentCreateRequest().content(content);
		commentUpdate = new CommentUpdateRequest().content("댓글2");
		
		comment1 = new CommentResponse()
					.content(content)
					.parentId(null);
	}
	
	@DisplayName("댓글을 생성하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentCreate_whenCallCreateComment_thenReturnComment() throws Exception {
		ResultActions action;
		
		given(commentService.create(any(Long.class), any(CommentCreateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {		
			comment1 = CommentMapper.INSTANCE.toDto(commentEntity);

			return comment1;
		});

		action = mockMvc.perform(
					post("/api/v1/posts/{post-id}/comments", postEntity.getId())
					.content(SerializeDeserializeUtil.serializeToString(commentCreate))
					.characterEncoding(StandardCharsets.UTF_8)
					.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.content", is(comment1.getContent())));
	}
	
	@DisplayName("내용이 없어 댓글을 생성하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentCreate_whenCallCreateComment_thenThrowBadRqeustExceptionForContent() throws Exception {
		ResultActions action;
		
		given(commentService.create(any(Long.class), any(CommentCreateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {		
			comment1 = CommentMapper.INSTANCE.toDto(commentEntity);

			return comment1;
		});
		
		commentCreate.content(null);
		action = mockMvc.perform(
			post("/api/v1/posts/{post-id}/comments", postEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 생성하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentCreate_whenCallCreateComment_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		given(commentService.create(any(Long.class), any(CommentCreateRequest.class), any(UserDetailsImpl.class))).willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));	
		
		action = mockMvc.perform(
			post("/api/v1/posts/{post-id}/comments", postEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);	

		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("인증되어 있지 않아 댓글을 생성하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallCreateComment_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		given(commentService.create(any(Long.class), any(CommentCreateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {		
			comment1 = CommentMapper.INSTANCE.toDto(commentEntity);

			return comment1;
		});
		
		action = mockMvc.perform(
			post("/api/v1/posts/{post-id}/comments", postEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteComment_thenReturnNothing() throws Exception {
		ResultActions action;
		
		willDoNothing().given(commentService).delete(any(Long.class), any(Long.class), any(UserDetailsImpl.class));
		
		action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteComment_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."})).given(commentService).delete(any(Long.class), any(Long.class), any(UserDetailsImpl.class));

		action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action;
		
		willThrow(new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."})).given(commentService).delete(any(Long.class), any(Long.class), any(UserDetailsImpl.class));

		action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotFoundException));
	}	

	@DisplayName("댓글을 생성한 사용자가 아니라서 삭제하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteComment_thenThrowUserNotMatchedException() throws Exception {
		ResultActions action;
		
		willThrow(new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 댓글을 생성한 사용자가 일치하지 않습니다."})).given(commentService).delete(any(Long.class), any(Long.class), any(UserDetailsImpl.class));
				
		action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotMatchedException));
	}
	
	@DisplayName("인증되어 있지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteComment_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		willDoNothing().given(commentService).delete(any(Long.class), any(Long.class), any(UserDetailsImpl.class));
		
		action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}	
	
	@DisplayName("댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallGetCommentsByPostId_thenReturnCommentsForPost() throws Exception {
		ResultActions action;
		CommentResponse comment2;
		PageResponse<CommentResponse> page;
		MultiValueMap<String, String> params;
		PaginationUtil pagination;
		
		comment2 = new CommentResponse()
					.content("실전 내용")
					.parentId(null);

		pagination = new PaginationUtil();
		page = PageResponse.handleResponse(List.of(comment1, comment2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, true, true);

		given(commentService.findAllByPostId(any(Long.class), any(PaginationUtil.class))).willReturn(page);

		params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		action = mockMvc.perform(
			get("/api/v1/posts/{post-id}/comments", postEntity.getId())
			.params(params)
			.characterEncoding(StandardCharsets.UTF_8)
		);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.size()", is(page.getContent().size())));
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentUpdate_whenCallUpdateComment_thenReturnComment() throws Exception {
		ResultActions action;
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			commentEntity.setContent(commentUpdate.getContent());
			comment1 = CommentMapper.INSTANCE.toDto(commentEntity);
			
			return comment1;
		});
	
		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", is(comment1.getContent())));
	}
	
	@DisplayName("내용이 없어 댓글을 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowBadRqeustExceptionForContent() throws Exception {
		ResultActions action;
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			commentEntity.setContent(commentUpdate.getContent());
			comment1 = CommentMapper.INSTANCE.toDto(commentEntity);
			
			return comment1;
		});
	
		commentUpdate.content(null);
		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowPostNotFoundException() throws Exception {		
		ResultActions action;
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class), any(UserDetailsImpl.class))).willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));

		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("댓글을 생성한 사용자가 아니라서 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowUserNotMatchedException() throws Exception {
		ResultActions action;
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class), any(UserDetailsImpl.class))).willThrow(new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 댓글을 생성한 사용자가 일치하지 않습니다."}));

		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotMatchedException));
	}
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser	
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action;
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class), any(UserDetailsImpl.class))).willThrow(new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));

		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotFoundException));
	}
	
	@DisplayName("인증되어 있지 않아 댓글을 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdateComment_thenUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		given(commentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			commentEntity.setContent(commentUpdate.getContent());
			comment1 = CommentMapper.INSTANCE.toDto(commentEntity);
			
			return comment1;
		});
	
		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
}