package com.whooa.blog.integration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.entity.PostEntity;
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
public class CommentIntegrationTest {
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
	
	private CategoryEntity categoryEntity;
	private PostEntity postEntity;
	private UserEntity userEntity;
	
	private CommentCreateRequest commentCreate1;
	private CommentUpdateRequest commentUpdate;

	private PaginationUtil pagination;
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();

		userEntity = userRepository.save(new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER));
		
		userRepository.save(new UserEntity()
				.email("real@real.com")
				.name("실전 이름")
				.password("1234")
				.userRole(UserRole.USER));
		
		categoryEntity = categoryRepository.save(new CategoryEntity().name("테스트 카테고리"));
		
		postEntity = postRepository.save(new PostEntity()
						.category(categoryEntity)
						.content("테스트 내용")
						.title("테스트 제목")
						.user(userEntity));
		
		userDetailsImpl = new UserDetailsImpl(userEntity);

		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	void setUpEach() {
		commentCreate1 = new CommentCreateRequest().content("테스트 내용");
		commentUpdate = new CommentUpdateRequest().content("실전 내용");
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
	
	@DisplayName("댓글을 생성하는데 성공한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentCreate_whenCallCreateComment_thenReturnComment() throws Exception {
		ResultActions action = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
										.content(SerializeDeserializeUtil.serialize(commentCreate1))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.content", is(commentCreate1.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 생성하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentCreate_whenCallCreateComment_thenThrowPostNotFoundException() throws Exception {				
		ResultActions action = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", 100L)
										.content(SerializeDeserializeUtil.serialize(commentCreate1))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("인증되어 있지 않아 댓글을 생성하는데 실패한다.")
	@Test
	public void givenCommentCreate_whenCallCreateComment_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
				.content(SerializeDeserializeUtil.serialize(commentCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("댓글을 삭제하는데 성공한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenReturnNothing() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
										.content(SerializeDeserializeUtil.serialize(commentCreate1))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON))
										.andExpect(status().isCreated())
										.andReturn();
		
		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), id));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowPostNotFoundException() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
									.content(SerializeDeserializeUtil.serialize(commentCreate1))
									.characterEncoding(StandardCharsets.UTF_8)
									.contentType(MediaType.APPLICATION_JSON))
									.andExpect(status().isCreated())
									.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", 100L, id));
		
		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(exception -> assertTrue(exception.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), 100L));
		
		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(output -> assertTrue(output.getResolvedException() instanceof CommentNotFoundException));
	}	

	@DisplayName("댓글을 생성한 사용자가 아니라서 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteComment_thenThrowUserNotMatchedException() throws Exception {
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("real@real.com").get());

		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
									.with(user(userDetailsImpl))
									.content(SerializeDeserializeUtil.serialize(commentCreate1))
									.characterEncoding(StandardCharsets.UTF_8)
									.contentType(MediaType.APPLICATION_JSON))
									.andExpect(status().isCreated())
									.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), id));
		
		action.andDo(print())
				.andExpect(exception -> assertTrue(exception.getResolvedException() instanceof UserNotMatchedException));
	}
	
	@DisplayName("인증되어 있지 않아 댓글을 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteComment_thenThrowUnauthenticatedUserException() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
										.with(user(userDetailsImpl))
										.content(SerializeDeserializeUtil.serialize(commentCreate1))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON))
										.andExpect(status().isCreated())
										.andReturn();
		
		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), id));
		
		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("포스트의 댓글 목록을 조회하는데 성공한다.")
	@Test
	public void givenPostId_whenCallGetCommentsByPostId_thenReturnCommentsForPost() throws Exception {		
		CommentCreateRequest commentCreate2 = new CommentCreateRequest().content("실전 내용");

		mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
				.with(user(userDetailsImpl))
				.content(SerializeDeserializeUtil.serialize(commentCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		 
		mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
				.with(user(userDetailsImpl))
				.content(SerializeDeserializeUtil.serialize(commentCreate2))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		ResultActions action = mockMvc.perform(get("/api/v1/posts/{post-id}/comments", postEntity.getId())
										.params(params)
										.characterEncoding(StandardCharsets.UTF_8));

		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(2)));
	}

	@DisplayName("댓글을 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenReturnComment() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
									.content(SerializeDeserializeUtil.serialize(commentCreate1))
									.characterEncoding(StandardCharsets.UTF_8)
									.contentType(MediaType.APPLICATION_JSON))
									.andExpect(status().isCreated())
									.andReturn();
		
		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), id)
										.content(SerializeDeserializeUtil.serialize(commentUpdate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content", is(commentUpdate.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아 댓글을 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowPostNotFoundException() throws Exception {		
		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
									.with(user(userDetailsImpl))
									.content(SerializeDeserializeUtil.serialize(commentCreate1))
									.characterEncoding(StandardCharsets.UTF_8)
									.contentType(MediaType.APPLICATION_JSON))
									.andExpect(status().isCreated())
									.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(patch("/api/v1/posts/{post-id}/comments/{id}", 100L, id)
										.content(SerializeDeserializeUtil.serialize(commentUpdate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
		
		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(output -> assertTrue(output.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("댓글이 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowCommentNotFoundException() throws Exception {
		ResultActions action = mockMvc.perform(patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), 100L)
										.contentType(MediaType.APPLICATION_JSON)
										.content(SerializeDeserializeUtil.serialize(commentUpdate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
		
		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(output -> assertTrue(output.getResolvedException() instanceof CommentNotFoundException));
	}
	
	@DisplayName("댓글을 생성한 사용자가 아니라서 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "test@test.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowUserNotMatchedException() throws Exception {
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("real@real.com").get());

		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
									.with(user(userDetailsImpl))
									.content(SerializeDeserializeUtil.serialize(commentCreate1))
									.characterEncoding(StandardCharsets.UTF_8)
									.contentType(MediaType.APPLICATION_JSON))
									.andExpect(status().isCreated())
									.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		ResultActions action = mockMvc.perform(patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), id)
										.content(SerializeDeserializeUtil.serialize(commentUpdate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
				
		action.andDo(print())
				.andExpect(output -> assertTrue(output.getResolvedException() instanceof UserNotMatchedException));
	}
	

	
	@DisplayName("인증되어 있지 않아 댓글을 수정하는데 실패한다.")
	@Test
	public void givenCommentUpdate_whenCallUpdateComment_thenThrowUnauthenticatedUserException() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/posts/{post-id}/comments", postEntity.getId())
									.with(user(userDetailsImpl))
									.content(SerializeDeserializeUtil.serialize(commentCreate1))
									.characterEncoding(StandardCharsets.UTF_8)
									.contentType(MediaType.APPLICATION_JSON))
									.andExpect(status().isCreated())
									.andReturn();

		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		ResultActions action = mockMvc.perform(patch("/api/v1/posts/{post-id}/comments/{id}", postEntity.getId(), id)
										.content(SerializeDeserializeUtil.serialize(commentUpdate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}
}