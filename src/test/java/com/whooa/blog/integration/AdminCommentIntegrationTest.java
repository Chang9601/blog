package com.whooa.blog.integration;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
@Testcontainers
public class AdminCommentIntegrationTest {
	@Container
	private static MySQLContainer mySqlContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0.33"));
	
	@DynamicPropertySource
	public static void setDynamicPropertySource(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.driverClassName", mySqlContainer::getDriverClassName);
		dynamicPropertyRegistry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySqlContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySqlContainer::getPassword);
	}
	
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

		userEntity = userRepository.save(
			new UserEntity()
			.email("admin@admin.com")
			.name("관리자")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.ADMIN)
		);
		
		userRepository.save(
			new UserEntity()
			.email("user1@user1.com")
			.name("사용자1")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.USER)
		);
		
		categoryEntity = categoryRepository.save(new CategoryEntity().name("카테고리"));
		
		postEntity = postRepository.save(
			new PostEntity()
			.category(categoryEntity)
			.content("포스트")
			.title("포스트")
			.user(userEntity)
		);

		new UserDetailsImpl(userEntity);
	}
	
	@BeforeEach
	void setUpEach() {	
		commentEntity = commentRepository.save(
			new CommentEntity()
			.content("댓글1")
			.post(postEntity)
			.user(userEntity)
		);
	
		commentUpdate = new CommentUpdateRequest().content("댓글2");
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
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
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
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
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
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
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
	@WithUserDetails(value = "user1@user1.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{post-id}/comments/{id}", postEntity.getId(), commentEntity.getId()));
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
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
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
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
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
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
	@WithUserDetails(value = "user1@user1.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
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