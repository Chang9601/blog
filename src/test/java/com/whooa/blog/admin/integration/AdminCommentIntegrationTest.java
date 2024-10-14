package com.whooa.blog.admin.integration;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.SerializeDeserializeUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Testcontainers
public class AdminCommentIntegrationTest {
//	/* 
//	 * Spring Boot 3.1 이전에는 Testcontainers가 자동으로 생성한 데이터베이스 주소를 설정하기 위해 DynamicPropertyRegistry를 사용해야 했다
//	 * Spring Boot 3.1 이후로 @ServiceConnection 애노테이션을 사용하여 해당 설정을 간단하게 할 수 있다.
//	 */
//	@Container
//	@ServiceConnection
//	private static MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0.33")
//															.withUsername("root")
//															.withPassword("root!@");
	
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private CategoryRepository categoryRepository;
    @Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	
	private CommentEntity commentEntity; 
	private CategoryEntity categoryEntity;
	private PostEntity postEntity;
	private UserEntity userEntity;
	
	private CommentUpdateRequest commentUpdate;
	
	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.addFilter(new CharacterEncodingFilter("utf-8", true))
					.apply(springSecurity()).build();

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("admin@naver.com");
		userEntity.setName("관리자");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.ADMIN);
		
		userRepository.save(userEntity);

		userEntity = new UserEntity();
		userEntity.setEmail("user1@naver.com");
		userEntity.setName("사용자1");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);
		
		userEntity = userRepository.save(userEntity);

		categoryEntity = new CategoryEntity();
		categoryEntity.setName("카테고리");
		
		categoryEntity = categoryRepository.save(categoryEntity);

		postEntity = new PostEntity();
		postEntity.setContent("포스트");
		postEntity.setTitle("포스트");
		postEntity.setCategory(categoryEntity);
		postEntity.setUser(userEntity);
		
		postEntity = postRepository.save(postEntity);

		new UserDetailsImpl(userEntity);
	}
	
	@BeforeEach
	void setUpEach() {
		commentUpdate = new CommentUpdateRequest();
		commentUpdate.setContent("댓글2");
		
		commentEntity = new CommentEntity();
		commentEntity.setContent("댓글1");
		commentEntity.setPost(postEntity);
		commentEntity.setUser(userEntity);

		commentEntity = commentRepository.save(commentEntity);
	}
	
	@AfterAll
	void tearDownAll() {
		commentRepository.deleteAll();
		postRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDownEach() {
		commentRepository.deleteAll();
	}
	   
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenReturnNothing() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", 100L, commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(exception -> assertTrue(exception.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), 100L));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof CommentNotFoundException));
	}
	
	@DisplayName("권한이 없어 댓글을 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenReturnComment() throws Exception {
		ResultActions action;
		
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
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", 100L, commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), 100L)
			.contentType(MediaType.APPLICATION_JSON)
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof CommentNotFoundException));
	}
	
	@DisplayName("권한이 없어 댓글을 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
			patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId())
			.content(SerializeDeserializeUtil.serializeToString(commentUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}	
}