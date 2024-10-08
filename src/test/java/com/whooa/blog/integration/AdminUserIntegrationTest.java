package com.whooa.blog.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import java.util.List;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminUserIntegrationTest {
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
	@Autowired
	private UserRepository userRepository;
	
	private UserEntity userEntity;

	private UserAdminUpdateRequest userAdminUpdate;
	
	private PaginationUtil pagination;
	private UserDetailsImpl userDetailsImpl;

	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();
		
		new UserDetailsImpl(
			userRepository.save(
				new UserEntity()
				.email("admin@admin.com")
				.name("관리자")
				.password("12345678Aa!@#$%")
				.userRole(UserRole.ADMIN)
			)
		);

		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	void setUpEach() {
		userEntity = userRepository.save(
			new UserEntity()
			.email("user1@user1.com")
			.name("사용자1")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.USER)
		);
		
		userRepository.save(
			new UserEntity()
			.email("user2@user2.com")
			.name("사용자2")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.USER)
		);
		
		userAdminUpdate = new UserAdminUpdateRequest()
							.email("user3@user3.com")
							.name("사용자3")
							.password("87654321Aa!@#$%")
							.userRole("MANAGER");
	}
	
	@AfterAll
	void tearDownAll() {
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDownEach() {
		List<UserEntity> userEntities;
		
		userEntities = userRepository.findAll();
		
		for (UserEntity userEntity: userEntities) {
			if (!userEntity.getEmail().equals("admin@admin.com")) {
				userRepository.delete(userEntity);
			}
		}
	}

	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenId_whenCallDeleteUser_thenReturnNothing() throws Exception {			
		ResultActions action;

		action = mockMvc.perform(delete("/api/v1/admin/users/{id}", userEntity.getId()).characterEncoding(StandardCharsets.UTF_8));
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("권한이 없어 사용자를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteUser_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("user1@user1.com").get());
		
		action = mockMvc.perform(
						delete("/api/v1/admin/users/{id}", userEntity.getId())
						.with(user(userDetailsImpl))
						.characterEncoding(StandardCharsets.UTF_8)
				);
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}	
	
	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenId_whenCallGetUser_thenReturnUser() throws Exception {
		ResultActions action;
				
		action = mockMvc.perform(get("/api/v1/admin/users/{id}", userEntity.getId()).characterEncoding(StandardCharsets.UTF_8));
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.email", is(userEntity.getEmail())));
	}
	
	@DisplayName("사용자가 존재하지 않아 조회하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenId_whenCallGetUser_thenUserNotFoundException() throws Exception {					
		ResultActions action;
		
		action = mockMvc.perform(get("/api/v1/admin/users/{id}", 100L).characterEncoding(StandardCharsets.UTF_8));
		
		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
	}	
	
	@DisplayName("권한이 없어 사용자를 조회하는데 실패한다.")
	@Test
	@WithUserDetails(value = "user1@user1.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenId_whenCallGetUser_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("user1@user1.com").get());

		action = mockMvc.perform(
						get("/api/v1/admin/users/{id}", userEntity.getId())
						.with(user(userDetailsImpl))
						.characterEncoding(StandardCharsets.UTF_8)
				);
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("사용자 목록을 조회하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenPagination_whenCallGetUsers_thenReturnUsers() throws Exception {
		ResultActions action;
		MultiValueMap<String, String> params; 
		
		params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		action = mockMvc.perform(
						get("/api/v1/admin/users")
						.params(params)
						.characterEncoding(StandardCharsets.UTF_8)
				);
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.content.size()", is(3)));
	}
	
	@DisplayName("권한이 없어 사용자 목록을 조회하는데 실패한다.")
	@Test
	public void givenPagination_whenCallGetUsers_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		MultiValueMap<String, String> params;
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("user1@user1.com").get());
		params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		action = mockMvc.perform(
						get("/api/v1/admin/users")
						.with(user(userDetailsImpl))
						.params(params)
						.characterEncoding(StandardCharsets.UTF_8)
				);
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("사용자를 수정하는데 성공한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenUserUpdate_whenCallUpdateUser_thenReturnUser() throws Exception {
		ResultActions action;		

		action = mockMvc.perform(
						patch("/api/v1/admin/users/{id}", userEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(userAdminUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.email", is(userAdminUpdate.getEmail())));
	}
	
	@DisplayName("이름이  짧아 사용자를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenUserUpdate_whenCallUpdateUser_thenThrowBadRequestExceptionForName() throws Exception {
		ResultActions action;
		
		userAdminUpdate.name("없");
		
		action = mockMvc.perform(
						patch("/api/v1/admin/users/{id}", userEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(userAdminUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("이메일이 유효하지 않아 사용자를 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenUserUpdate_whenCallUpdateUser_thenThrowBadRequestExceptionForEmail() throws Exception {
		ResultActions action;
		
		userAdminUpdate.email("user3user3.com");
		
		action = mockMvc.perform(
						patch("/api/v1/admin/users/{id}", userEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(userAdminUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);

		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("사용자가 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenUserUpdate_whenCallUpdateUser_thenThrowUserNotFoundException() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
						patch("/api/v1/admin/users/{id}", 100L)
						.content(SerializeDeserializeUtil.serializeToString(userAdminUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
	}
	
	@DisplayName("사용자 이미 존재하여 수정하는데 실패한다.")
	@Test
	@WithUserDetails(value = "admin@admin.com", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")	
	public void givenUserUpdate_whenCallUpdateUser_thenThrowDuplicateUserException() throws Exception {
		ResultActions action;
		
		userAdminUpdate.email("user2@user2.com");
		
		action = mockMvc.perform(
						patch("/api/v1/admin/users/{id}", userEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(userAdminUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isConflict());
	}
	
	@DisplayName("권한이 없어 사용자를 수정하는데 성공한다.")
	@Test
	public void givenUserUpdate_whenCallUpdateUser_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue("user1@user1.com").get());

		action = mockMvc.perform(
						patch("/api/v1/admin/users/{id}", userEntity.getId())
						.with(user(userDetailsImpl))
						.content(SerializeDeserializeUtil.serializeToString(userAdminUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}	
}