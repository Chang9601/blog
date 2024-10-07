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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.admin.controller.AdminUserController;
import com.whooa.blog.admin.service.AdminUserService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

@WebMvcTest(controllers = {AdminUserController.class})
@ContextConfiguration(classes = {AdminUserController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AllExceptionHandler.class)
public class AdminUserControllerTest {
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@MockBean
	private AdminUserService adminUserService;
	
	private UserEntity userEntity;

	private UserResponse user1;

	@BeforeAll
	public void setUpAll() {		
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.addFilter(new CharacterEncodingFilter("utf-8", true))
					.apply(springSecurity()).build();
	}
	
	@BeforeEach
	public void setUpEach() {
		String email, name, password;
		
		email = "user1@user1.com";
		name = "사용자1";
		password = "12345678Aa!@#$%";
		
		userEntity = new UserEntity();
		userEntity.setEmail(email);
		userEntity.setName(name);
		userEntity.setPassword(password);
		userEntity.setUserRole(UserRole.USER);
	
		user1 = new UserResponse();
		user1.setEmail(userEntity.getEmail());
		user1.setUserRole(userEntity.getUserRole());
	}
	
	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteUser_thenReturnNothing() throws Exception {
		ResultActions action;
		
		willDoNothing().given(adminUserService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/users/{id}", userEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}	

	@DisplayName("사용자가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteUser_thenThrowUserNotFoundException() throws Exception {		
		ResultActions action;
		
		willThrow(new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."})).given(adminUserService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/users/{id}", 100L));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
	}
	
	@DisplayName("사용자를 삭제하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteUser_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		willDoNothing().given(adminUserService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/users/{id}", userEntity.getId()));
		
		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}	
	
	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallGetUser_thenReturnUser() throws Exception {
		ResultActions action;
		
		given(adminUserService.find(any(Long.class))).willReturn(user1);

		action = mockMvc.perform(
			get("/api/v1/admin/users/{id}", userEntity.getId())
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email", is(user1.getEmail())));
	}
	
	@DisplayName("사용자가 존재하지 않아 조회하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallGetUser_thenThrowUserNotFoundException() throws Exception {
		ResultActions action;
		
		given(adminUserService.find(any(Long.class))).willThrow(new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));

		action = mockMvc.perform(
			get("/api/v1/admin/users/{id}", userEntity.getId())
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
	}
	
	@DisplayName("사용자를 조회하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallGetUser_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		given(adminUserService.find(any(Long.class))).willReturn(user1);

		action = mockMvc.perform(
			get("/api/v1/admin/users/{id}", userEntity.getId())
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isForbidden());
	}
	
	@DisplayName("사용자 목록을 조회하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPagination_whenCallGetUsers_thenReturnUsers() throws Exception {
		ResultActions action;
		PageResponse<UserResponse> page;
		MultiValueMap<String, String> params;
		UserResponse user2;
		PaginationUtil pagination;
		
		user2 = UserResponse.builder()
					.email("user2@naver.com")
					.userRole(UserRole.USER)
					.build();
		
		pagination = new PaginationUtil();
		page = PageResponse.handleResponse(List.of(user1, user2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, true, true);

		given(adminUserService.findAll(any(PaginationUtil.class))).willReturn(page);

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
			.andExpect(jsonPath("$.data.content.size()", is(page.getContent().size())));
	}
	
	@DisplayName("사용자 목록을 조회하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPagination_whenCallGetUsers_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		PageResponse<UserResponse> page;
		MultiValueMap<String, String> params;
		UserResponse user2;
		PaginationUtil pagination;

		user2 = UserResponse.builder()
					.email("user2@naver.com")
					.userRole(UserRole.USER)
					.build();
	
		pagination = new PaginationUtil();
		page = PageResponse.handleResponse(List.of(user1, user2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, true, true);

		given(adminUserService.findAll(any(PaginationUtil.class))).willReturn(page);

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
			.andExpect(status().isForbidden());
	}	
}