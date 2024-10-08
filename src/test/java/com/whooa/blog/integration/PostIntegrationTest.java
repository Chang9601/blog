package com.whooa.blog.integration;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.jayway.jsonpath.JsonPath;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostIntegrationTest {
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;

	private CategoryEntity categoryEntity;
	private UserEntity userEntity;

	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;

	private UserDetailsImpl userDetailsImpl;

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
		userEntity.setEmail("user1@naver.com");
		userEntity.setName("사용자1");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);
		
		userEntity = userRepository.save(userEntity);
		
		userEntity = new UserEntity();
		userEntity.setEmail("user2@naver.com");
		userEntity.setName("사용자2");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);
		
		userRepository.save(userEntity);
		
		userDetailsImpl = new UserDetailsImpl(userEntity);
	}
	
	@BeforeEach
	void setUpEach() {		
		postCreate = new PostCreateRequest();
		postCreate.setCategoryName(categoryEntity.getName());
		postCreate.setContent(generateRandomString(200));
		postCreate.setTitle("포스트1");
				
		postUpdate = new PostUpdateRequest();
		
		postUpdate.setCategoryName(categoryEntity.getName());
		postUpdate.setContent(generateRandomString(200));
		postUpdate.setTitle("포스트2");
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

	@DisplayName("포스트(파일 X)를 생성하는데 성공한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostCreate_whenCallCreatePost_thenReturnPost() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
					
		action = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);
		
		action
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.title", is(postCreate.getTitle())))
			.andExpect(jsonPath("$.data.content", is(postCreate.getContent())));
	}
	
	@DisplayName("포스트(파일 O)를 생성하는데 성공한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostCreate_whenCallCreatePost_thenReturnPostWithFiles() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile, postFile1, postFile2;
		
		postFile1 = new MockMultipartFile("files", "test1.txt", MediaType.TEXT_PLAIN_VALUE, "test1".getBytes(StandardCharsets.UTF_8));
		postFile2 = new MockMultipartFile("files", "test2.txt", MediaType.TEXT_PLAIN_VALUE, "test2".getBytes(StandardCharsets.UTF_8));
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
					
		action = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.file(postFile1)
			.file(postFile2)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);
		
		action
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.title", is(postCreate.getTitle())))
			.andExpect(jsonPath("$.data.content", is(postCreate.getContent())))
			.andExpect(jsonPath("$.data.files.length()", is(2)));
	}
	
	@DisplayName("카테고리 이름이 짧아 포스트를 생성하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostCreate_whenCallCreatePost_thenThrowBadRequestExceptionForCategoryName() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile;
		
		postCreate.setCategoryName("카");
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
					
		action = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);
		
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("제목이 짧아 포스트를 생성하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostCreate_whenCallCreatePost_thenThrowBadRequestExceptionForTitle() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile;
		
		postCreate.setTitle("포");
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
					
		action = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);
		
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("내용이 짧아 포스트를 생성하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostCreate_whenCallCreatePost_thenThrowBadRequestExceptionForContent() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile;
		
		postCreate.setContent("포");
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
					
		action = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);
		
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("인증되어 있지 않아 포스트를 생성하는데 실패한다.")
	@Test
	public void givenPostCreate_whenCallCreatePost_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		action = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeletePost_thenReturnNothing() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile;
		MvcResult result;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		action = mockMvc.perform(delete("/api/v1/posts/{id}", id));

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeletePost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/posts/{id}", 100L));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("포스트를 생성한 사용자가 아니라서 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeletePost_thenThrowUserNotMatchedException() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile;
		MvcResult result;
		
		userDetailsImpl =  new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("user2@naver.com").get());
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(delete("/api/v1/posts/{id}", id));
		
		action
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof UserNotMatchedException));
	}	
	
	@DisplayName("인증되어 있지 않아 포스트를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeletePost_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/posts/{id}", 100L));

		action
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("포스트를 조회하는데 성공한다")
	@Test
	public void givenId_whenCallGetPost_thenReturnPost() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile;
		MvcResult result;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		
		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			get("/api/v1/posts/{id}", id)
			.characterEncoding(StandardCharsets.UTF_8)
		);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.title", is(postCreate.getTitle())))
			.andExpect(jsonPath("$.data.content", is(postCreate.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallGetPost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
			get("/api/v1/posts/{id}", 100L)
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallGetPosts_thenReturnPosts() throws Exception {
		ResultActions action;
		MockMultipartFile postCreateFile1, postCreateFile2;
		MultiValueMap<String, String> params;
		PaginationUtil pagination;

		postCreateFile1 = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postCreateFile2 = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile1)
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated()
		);
		
		mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile2)
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated()
		);
		
		pagination = new PaginationUtil();
		
		params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		action = mockMvc.perform(
			get("/api/v1/posts")
			.params(params)
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content.size()", is(2)));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowUserNotMatchedException() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postUpdateFile;
		MvcResult result;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
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
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPostWithFiles() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postFile, postUpdateFile;
		MvcResult result;
		
		postFile = new MockMultipartFile("files", "test1.txt", MediaType.TEXT_PLAIN_VALUE, "test1".getBytes(StandardCharsets.UTF_8));
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
			.file(postUpdateFile)
			.file(postFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA));

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.title", is(postUpdate.getTitle())))
			.andExpect(jsonPath("$.data.content", is(postUpdate.getContent())))
			.andExpect(jsonPath("$.data.files.length()", is(1)));
	}
	
	@DisplayName("카테고리 이름이 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForCategoryName() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postUpdateFile;
		MvcResult result;
		
		postUpdate.setCategoryName("카");
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
	
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
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
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForTitle() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postUpdateFile;
		MvcResult result;
		
		postUpdate.setTitle("포");
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
					
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
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
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForContent() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postUpdateFile;
		MvcResult result;
		
		postUpdate.setContent("포");
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
		
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
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
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", 100L)
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("포스트를 생성한 사용자가 아니라서 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@naver.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPost() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postUpdateFile;
		MvcResult result;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		userDetailsImpl =  new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("user2@naver.com").get());

		result = mockMvc.perform(
			multipart(HttpMethod.POST, "/api/v1/posts")
			.file(postCreateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
			.file(postUpdateFile)
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA)
		);

		action
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof UserNotMatchedException));	
	}
	
	@DisplayName("인증되어 있지 않아 포스트를 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdatePost_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		Integer id;
		MockMultipartFile postCreateFile, postUpdateFile;
		MvcResult result;
		
		postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		result = mockMvc.perform(
			multipart("/api/v1/posts")
			.file(postCreateFile)
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andReturn();
				
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
			multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", id)
			.file(postUpdateFile)
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.MULTIPART_FORM_DATA));

		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}		
}