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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.admin.controller.AdminCommentController;
import com.whooa.blog.admin.service.AdminCommentService;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.SerializeDeserializeUtil;

@WebMvcTest(controllers = {AdminCommentController.class})
@ContextConfiguration(classes = {AdminCommentController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AllExceptionHandler.class)
public class AdminCommentControllerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@MockBean
	private AdminCommentService adminCommentService;

	private CommentEntity commentEntity;
	private CategoryEntity categoryEntity;
	private PostEntity postEntity;
	private UserEntity userEntity;
	
	private CommentUpdateRequest commentUpdate;
	private CommentResponse comment;
	
	@BeforeAll
	public void setUpAll() {
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.addFilter(new CharacterEncodingFilter("utf-8", true))
					.apply(springSecurity()).build();
		
		userEntity = new UserEntity();
		userEntity.setEmail("user1@naver.com");
		userEntity.setName("사용자1");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);
						
		categoryEntity = new CategoryEntity();
		categoryEntity.setName("카테고리");
	
		postEntity = new PostEntity();
		postEntity.setContent("포스트");
		postEntity.setTitle("포스트");
		postEntity.setCategory(categoryEntity);
		postEntity.setUser(userEntity);
	}
	
	@BeforeEach
	public void setUpEach() {		
		commentUpdate = new CommentUpdateRequest();
		commentUpdate.setContent("댓글2");
		
		commentEntity = new CommentEntity();
		
		commentEntity.setContent("댓글1");
		commentEntity.setPost(postEntity);
		commentEntity.setUser(userEntity);
	
		comment = new CommentResponse();
		comment.setId(commentEntity.getId());
		comment.setContent(commentEntity.getContent());
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteComment_thenReturnNothing() throws Exception {
		ResultActions action;
		
		willDoNothing().given(adminCommentService).delete(any(Long.class), any(Long.class));
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteComment_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."})).given(adminCommentService).delete(any(Long.class), any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action;
		
		willThrow(new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."})).given(adminCommentService).delete(any(Long.class), any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotFoundException));
	}	
	
	@DisplayName("필요한 권한이 없어 댓글을 삭제하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteComment_thenThrowUnauthorizeUserException() throws Exception {
		ResultActions action;
		
		willDoNothing().given(adminCommentService).delete(any(Long.class), any(Long.class));
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCommentUpdate_whenCallUpdateComment_thenReturnComment() throws Exception {
		ResultActions action;
		
		given(adminCommentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willAnswer((answer) -> {
			commentEntity.setContent(commentUpdate.getContent());
			
			comment.setContent(commentEntity.getContent());
			
			return CommentMapper.INSTANCE.fromEntity(commentEntity);
		});
	
		action = mockMvc.perform(
			patch("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content", is(commentUpdate.getContent())));
	}
	
	@DisplayName("내용이 없어 댓글을 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowBadRqeustExceptionForContent() throws Exception {
		ResultActions action;
		
		given(adminCommentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willAnswer((answer) -> {
			commentEntity.setContent(commentUpdate.getContent());
			
			comment.setContent(commentEntity.getContent());
			
			return CommentMapper.INSTANCE.fromEntity(commentEntity);
		});
	
		commentUpdate.setContent(null);
		
		action = mockMvc.perform(
			patch("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
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
	@WithMockCustomAdmin
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowPostNotFoundException() throws Exception {	
		ResultActions action;
		
		given(adminCommentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));

		action = mockMvc.perform(
			patch("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action;
		
		given(adminCommentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willThrow(new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));

		action = mockMvc.perform(
			patch("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
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
	
	@DisplayName("필요한 권한이 없어 댓글을 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCommentUpdate_whenCallUpdateComment_thenUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		given(adminCommentService.update(any(Long.class), any(Long.class), any(CommentUpdateRequest.class))).willAnswer((answer) -> {
			commentEntity.setContent(commentUpdate.getContent());
			
			comment.setContent(commentEntity.getContent());
			
			return CommentMapper.INSTANCE.fromEntity(commentEntity);
		});
	
		action = mockMvc.perform(
			patch("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}	
}