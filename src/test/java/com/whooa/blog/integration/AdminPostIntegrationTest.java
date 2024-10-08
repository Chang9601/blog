package com.whooa.blog.integration;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Random;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
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
public class AdminPostIntegrationTest {
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;
	
	private PostEntity postEntity;
	private CategoryEntity categoryEntity;
	private UserEntity userEntity;
	
	private PostUpdateRequest postUpdate;
	
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private static String generateRandomString(int length) {
		Random random;
		StringBuilder stringBuilder;
		int i;
		
		random = new Random();
		stringBuilder = new StringBuilder(length);

		for (i = 0; i < length; i++) {
			stringBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		
		return stringBuilder.toString();
	}
	
	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.addFilter(new CharacterEncodingFilter("utf-8", true))
					.apply(springSecurity()).build();
		
		categoryEntity = new CategoryEntity();
		categoryEntity.setName("카테고리");
		
		categoryEntity = categoryRepository.save(categoryEntity);

		userEntity = new UserEntity();
		userEntity.setEmail("admin@naver.com");
		userEntity.setName("관리자");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.ADMIN);
		
		userRepository.save(userEntity);

		userEntity = new UserEntity();
		userEntity.setEmail("user@naver.com");
		userEntity.setName("사용자");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);
		
		userEntity = userRepository.save(userEntity);

	}

	@BeforeEach
	void setUpEach() {
		postUpdate = new PostUpdateRequest();
		postUpdate.setCategoryName(categoryEntity.getName());
		postUpdate.setContent(generateRandomString(200));
		
		postEntity = new PostEntity();
		postEntity.setContent("포스트");
		postEntity.setTitle("포스트");
		postEntity.setCategory(categoryEntity);
		postEntity.setUser(userEntity);
		
		postEntity = postRepository.save(postEntity);
	}
	
	@AfterAll
	void tearDownAll() {
		postRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDownEach() {
		postRepository.deleteAll();
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeletePost_thenReturnNothing() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{id}", postEntity.getId()));

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeletePost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/posts/{id}", 100L));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("권한이 없어 포스트를 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeletePost_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{id}", postEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPost() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;

		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.title", is(postUpdate.getTitle())))
			.andExpect(jsonPath("$.data.content", is(postUpdate.getContent())));
	}
	
	@DisplayName("포스트(파일 O)를 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPostWithFiles() throws Exception {
		ResultActions action;
		MockMultipartFile postFile;
		MockMultipartFile postUpdateFile;

		postFile = new MockMultipartFile("files", "test1.txt", MediaType.TEXT_PLAIN_VALUE, "test1".getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.file(postFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.title", is(postUpdate.getTitle())))
			.andExpect(jsonPath("$.data.content", is(postUpdate.getContent())))
			.andExpect(jsonPath("$.data.files.length()", is(1)));
	}
	
	@DisplayName("카테고리 이름이 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForCategoryName() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdate.setCategoryName("카");
	
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest());		
	}
	
	@DisplayName("제목이 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForTitle() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdate.setTitle("포");

		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("내용이 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForContent() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdate.setContent("포스트2");
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", 100L)
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("권한이 없어 포스트를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}
}