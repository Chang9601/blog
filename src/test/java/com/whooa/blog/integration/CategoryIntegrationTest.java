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

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryUpdateRequest;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.exception.DuplicateCategoryException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryIntegrationTest {
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;

	private CategoryCreateRequest categoryCreate1;
	private CategoryUpdateRequest categoryUpdate;
	
	private PaginationUtil pagination;
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();
		
		userRepository.save(
			new UserEntity()
			.email("user@user.com")
			.name("사용자")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.USER)
		);
		
		userRepository.save(
			new UserEntity()
			.email("admin@admin.com")
			.name("관리자")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.ADMIN)
		);		
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("admin@admin.com").get());
		
		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	void setUpEach() {
		categoryCreate1 = new CategoryCreateRequest().name("카테고리1");
		categoryUpdate = new CategoryUpdateRequest().name("카테고리2");		
	}

	@AfterAll
	void tearDownAll() {
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDownEach() {
		categoryRepository.deleteAll();
	}
	
	@DisplayName("카테고리를 생성하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryCreate_whenCallCreateCategory_thenReturnCategory() throws Exception {
		ResultActions action = mockMvc.perform(
										post("/api/v1/categories")
										.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON)
								);
		
		action
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.data.name", is(categoryCreate1.getName())));
	}

	@DisplayName("이름이  짧아 카테고리를 생성하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryCreate_whenCallCreateCategory_thenThrowBadRqeustExceptionForName() throws Exception {
		ResultActions action;
		categoryCreate1.name("카");
		
		action = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("카테고리가 이미 존재하여 생성하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryCreate_whenCallCreateCategory_thenThrowDuplicateCategoryException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
				post("/api/v1/categories")
				.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
		);
		
		action = mockMvc.perform(
				post("/api/v1/categories")
				.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
		.andDo(print())
		.andExpect(status().isConflict())
		.andExpect(output -> assertTrue(output.getResolvedException() instanceof DuplicateCategoryException));
	}
	
	@DisplayName("카테고리를 생성하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithUserDetails(value = "user@user.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryCreate_whenCallCreateCategory_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("카테고리를 삭제하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteCategory_thenReturnNothing() throws Exception {
		ResultActions action;
		Integer id;
		MvcResult result;
		
		result = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andReturn();
		
		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		action = mockMvc.perform(delete("/api/v1/categories/{id}", id));
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}

	@DisplayName("카테고리가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteCategory_thenThrowCategoryNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(delete("/api/v1/categories/{id}", 100L));
			        
		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(output -> assertTrue(output.getResolvedException() instanceof CategoryNotFoundException));
	}

	@DisplayName("권한이 없어 카테고리를 삭제하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user@user.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenId_whenCallDeleteCategory_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		Integer id;
		MvcResult result;
		
		result = mockMvc.perform(
						post("/api/v1/categories")
						.with(user(userDetailsImpl))
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andReturn();

		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(delete("/api/v1/categories/{id}", id));
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("카테고리 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallGetCategories_thenReturnCategories() throws Exception {
		ResultActions action;
		CategoryCreateRequest categoryCreate2;
		MultiValueMap<String, String> params;
		
		categoryCreate2 = new CategoryCreateRequest().name("실전 카테고리");
		
		mockMvc.perform(post("/api/v1/categories")
				.with(user(userDetailsImpl))
				.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		
		mockMvc.perform(post("/api/v1/categories")
				.with(user(userDetailsImpl))
				.content(SerializeDeserializeUtil.serializeToString(categoryCreate2))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();

		params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		action = mockMvc.perform(
						get("/api/v1/categories")
						.params(params)
						.characterEncoding(StandardCharsets.UTF_8)
				);
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.content.size()", is(2)));
	}
	
	@DisplayName("카테고리를 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryUpdate_whenCallUpdateCategory_thenReturnCategory() throws Exception {
		ResultActions action;
		Integer id;
		MvcResult result;
		
		result = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andReturn();

		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", id)
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.name", is(categoryUpdate.getName())));
	}
	
	@DisplayName("이름이 짧아 카테고리를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryUpdate_whenCallUpdateCategory_thenThrowBadRqeustExceptionForName() throws Exception {
		ResultActions action;
		Integer id;
		MvcResult result;
		
		categoryUpdate.name("카");
		
		result = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andReturn();

		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");

		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", id)
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("카테고리가 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryUpdate_whenCallUpdateCategory_thenThrowCategoryNotFoundException() throws Exception {				
		ResultActions action;
		
		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", 100L)
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(output -> assertTrue(output.getResolvedException() instanceof CategoryNotFoundException));
	}
	
	@DisplayName("권한이 없어 카테고리를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user@user.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
	public void givenCategoryUpdate_whenCallUpdateCategory_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		Integer id;
		MvcResult result;
		
		result = mockMvc.perform(
						post("/api/v1/categories")
						.with(user(userDetailsImpl))
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate1))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated())
						.andReturn();

		id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", id)
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
}