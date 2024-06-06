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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.jayway.jsonpath.JsonPath;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS) 
class UserIntegrationTest {
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private UserRepository userRepository;
	
	private UserCreateRequest userCreate1;
	
	private PaginationUtil pagination;
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();;
		
		userRepository.save(new UserEntity()
				.email("admin@admin.com")
				.name("관리자 이름")
				.password("1234")
				.userRole(UserRole.ADMIN));

		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	void setUpEach() {
		String email = "test@test.com";
		String name = "테스트 이름";
		String password = "12345678Aa!@#$%";
		String userRole = "USER";

		userCreate1 = new UserCreateRequest()
							.email(email)
							.name(name)
							.password(password)
							.userRole(userRole);
	}
	
	@AfterAll
	void tearDownAll() {
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDownEach() {
		if (userRepository.existsByEmail("test@test.com")) {		
			userRepository.delete(userRepository.findByEmail("test@test.com").get());
		}
	}

	@DisplayName("사용자를 생성하는데 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenReturnUser() throws Exception {
		ResultActions action = mockMvc.perform(post("/api/v1/users")
								.content(SerializeDeserializeUtil.serialize(userCreate1))
								.characterEncoding(StandardCharsets.UTF_8)
								.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.email", is(userCreate1.getEmail())))
				.andExpect(jsonPath("$.data.userRole", is(userCreate1.getUserRole())));
	}
	
	@DisplayName("이름이 너무 짧아 사용자를 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowBadRequestExceptionForName() throws Exception {
		userCreate1.name("테");
		ResultActions action = mockMvc.perform(post("/api/v1/users")
								.content(SerializeDeserializeUtil.serialize(userCreate1))
								.characterEncoding(StandardCharsets.UTF_8)
								.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("이메일이 유효하지 않아 사용자를 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowBadRequestExceptionForEmail() throws Exception {
		userCreate1.email("testtest.com");
		ResultActions action = mockMvc.perform(post("/api/v1/users")
								.content(SerializeDeserializeUtil.serialize(userCreate1))
								.characterEncoding(StandardCharsets.UTF_8)
								.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("비밀번호가 유효하지 않아 사용자를 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowBadRequestExceptionForPassword() throws Exception {
		userCreate1.password("12341231");
		ResultActions action = mockMvc.perform(post("/api/v1/users")
								.content(SerializeDeserializeUtil.serialize(userCreate1))
								.characterEncoding(StandardCharsets.UTF_8)
								.contentType(MediaType.APPLICATION_JSON));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	@DisplayName("사용자 이미 존재하여 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowDuplicateUserException() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		ResultActions action = mockMvc.perform(post("/api/v1/users")
								.content(SerializeDeserializeUtil.serialize(userCreate1))
								.characterEncoding(StandardCharsets.UTF_8)
								.contentType(MediaType.APPLICATION_JSON));
		
		action.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(output -> assertTrue(output.getResolvedException() instanceof DuplicateUserException));
	}
	
	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDeleteUser_thenReturnNothing() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("test@test.com").get());

		ResultActions action = mockMvc.perform(delete("/api/v1/users")
										.with(user(userDetailsImpl)));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("인증되어 있지 않아 사용자를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteUser_thenThrowUnauthenticatedUserException() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		ResultActions action = mockMvc.perform(delete("/api/v1/users"));
		
		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("본인을 조회하는데 성공한다.")
	@Test
	public void givenNothing_whenCallGetMe_thenReturnUser() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("test@test.com").get());

		ResultActions action = mockMvc.perform(get("/api/v1/users/me")
										.with(user(userDetailsImpl))
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email", is(userCreate1.getEmail())));
	}
	
	@DisplayName("인증되어 있지 않아 본인을 조회하는데 실패한다.")
	@Test
	public void givenNothing_whenCallGetMe_thenThrowUnauthenticatedUserException() throws Exception {		
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		ResultActions action = mockMvc.perform(get("/api/v1/users/me")
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallGetUser_thenReturnUser() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		
		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("admin@admin.com").get());

		ResultActions action = mockMvc.perform(get("/api/v1/users/{id}", id)
										.with(user(userDetailsImpl))
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email", is(userCreate1.getEmail())));
	}
	
	@DisplayName("사용자를 조회하는데 필요한 권한이 없어 실패한다.")
	@Test
	public void givenId_whenCallGetUser_thenThrowUnauthorizedUserException() throws Exception {		
		MvcResult result = mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		
		Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("test@test.com").get());

		ResultActions action = mockMvc.perform(get("/api/v1/users/{id}", id)
										.with(user(userDetailsImpl))
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@DisplayName("사용자 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallGetUsers_thenReturnUsers() throws Exception {	
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("admin@admin.com").get());

		ResultActions action = mockMvc.perform(get("/api/v1/users")
										.with(user(userDetailsImpl))
										.params(params)
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(2)));
	}
	
	@DisplayName("사용자 목록을 조회하는데 필요한 권한이 없어 실패한다.")
	@Test
	public void givenPagination_whenCallGetUsers_thenThrowUnauthorizedUserException() throws Exception {	
		mockMvc.perform(post("/api/v1/users")
				.content(SerializeDeserializeUtil.serialize(userCreate1))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
			
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("test@test.com").get());
		
		ResultActions action = mockMvc.perform(get("/api/v1/users")
										.with(user(userDetailsImpl))
										.params(params)
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isForbidden());
	}	
}