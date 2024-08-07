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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.controller.UserController;
import com.whooa.blog.user.dto.UserDTO.UserCreateRequest;
import com.whooa.blog.user.dto.UserDTO.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.mapper.UserMapper;
import com.whooa.blog.user.service.UserService;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.PasswordUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;
import com.whooa.blog.util.UserRoleMapper;

@WebMvcTest(controllers = {UserController.class})
@ContextConfiguration(classes = {UserController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AllExceptionHandler.class)
public class UserControllerTest {
	private MockMvc mockMvc;
	
    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@MockBean
	private UserService userService;

	private UserEntity userEntity;

	private UserCreateRequest userCreate;
	private UserResponse user1;
	
	private PaginationUtil pagination;
	
	@BeforeAll
	public void setUpAll() {		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();
		
		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	public void setUpEach() {
		String email = "ttest@test.com";
		String name = "테테스트 이름";
		String password = "12345678Aa!@#$%";
		String userRole = "USER";
		
		userEntity = new UserEntity()
				.email(email)
				.name(name)
				.password(password)
				.userRole(UserRole.USER);
		
		userCreate = new UserCreateRequest()
				.email(email)
				.name(name)
				.password(password)
				.userRole(userRole);
		
		user1 = new UserResponse()
				.email(email)
				.userRole(UserRole.USER);
	}
	
	@DisplayName("사용자를 생성하는데 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenReturnUser() throws Exception {
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			userEntity.password(PasswordUtil.hash(userEntity.getPassword())).userRole(UserRoleMapper.map(userCreate.getUserRole()));
			user1 = UserMapper.INSTANCE.toDto(userEntity);
			
			return user1;
		});

		ResultActions action = mockMvc.perform(post("/api/v1/users")
										.content(SerializeDeserializeUtil.serializeToString(userCreate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
				
		action.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.email", is(user1.getEmail())))
				.andExpect(jsonPath("$.data.userRole", is(user1.getUserRole().getRole())));
	}
	
	@DisplayName("이름이 너무 짧아 사용자를 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowBadRequestExceptionForName() throws Exception {
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			userEntity.password(PasswordUtil.hash(userEntity.getPassword())).userRole(UserRoleMapper.map(userCreate.getUserRole()));
			user1 = UserMapper.INSTANCE.toDto(userEntity);
			
			return user1;
		});

		userCreate.name("테");
		ResultActions action = mockMvc.perform(post("/api/v1/users")
										.content(SerializeDeserializeUtil.serializeToString(userCreate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
				
		action.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@DisplayName("이메일이 유효하지 않아 사용자를 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowBadRequestExceptionForEmail() throws Exception {
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			userEntity.password(PasswordUtil.hash(userEntity.getPassword())).userRole(UserRoleMapper.map(userCreate.getUserRole()));
			user1 = UserMapper.INSTANCE.toDto(userEntity);
			
			return user1;
		});

		userCreate.email("ttesttest.com");
		ResultActions action = mockMvc.perform(post("/api/v1/users")
										.content(SerializeDeserializeUtil.serializeToString(userCreate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
				
		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("비밀번호가 유효하지 않아 사용자를 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowBadRequestExceptionForPassword() throws Exception {
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			userEntity.password(PasswordUtil.hash(userEntity.getPassword())).userRole(UserRoleMapper.map(userCreate.getUserRole()));
			user1 = UserMapper.INSTANCE.toDto(userEntity);
			
			return user1;
		});

		userCreate.password("12314241");
		ResultActions action = mockMvc.perform(post("/api/v1/users")
										.content(SerializeDeserializeUtil.serializeToString(userCreate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
				
		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("사용자 이미 존재하여 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateUser_thenThrowDuplicateUserException() throws Exception {
		given(userService.create(any(UserCreateRequest.class))).willThrow(new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."}));

		ResultActions action = mockMvc.perform(post("/api/v1/users")
										.content(SerializeDeserializeUtil.serializeToString(userCreate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));
		action.andDo(print())
				.andExpect(status().isConflict())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateUserException));
	}
	
	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteUser_thenReturnNothing() throws Exception {		
		willDoNothing().given(userService).delete(any(UserDetailsImpl.class));

		ResultActions action = mockMvc.perform(delete("/api/v1/users"));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	// TODO: UnauthenticatedUserException 예외 클래스로 처리하는 방법.
	@DisplayName("인증되어 있지 않아 사용자를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeleteUser_thenThrowUnauthenticatedUserException() throws Exception {		
		willDoNothing().given(userService).delete(any(UserDetailsImpl.class));

		ResultActions action = mockMvc.perform(delete("/api/v1/users"));
		
		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("본인을 조회하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenNothing_whenCallGetMe_thenReturnUser() throws Exception {
		given(userService.find(any(UserDetailsImpl.class))).willReturn(user1);

		ResultActions action = mockMvc.perform(get("/api/v1/users/me")
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email", is(user1.getEmail())));
	}
	
	@DisplayName("인증되어 있지 않아 본인을 조회하는데 실패한다.")
	@Test
	public void givenNothing_whenCallGetMe_thenThrowUnauthenticatedUserException() throws Exception {		
		given(userService.find(any(UserDetailsImpl.class))).willReturn(user1);

		ResultActions action = mockMvc.perform(get("/api/v1/users/me")
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallGetUser_thenReturnUser() throws Exception {
		given(userService.findById(any(Long.class))).willReturn(user1);

		ResultActions action = mockMvc.perform(get("/api/v1/users/{id}", userEntity.getId())
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.email", is(user1.getEmail())));
	}
	
	@DisplayName("사용자를 조회하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallGetUser_thenThrowUnauthorizedUserException() throws Exception {		
		given(userService.findById(any(Long.class))).willReturn(user1);

		ResultActions action = mockMvc.perform(get("/api/v1/users/{id}", userEntity.getId())
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isForbidden());
	}
	
	@DisplayName("사용자 목록을 조회하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPagination_whenCallGetUsers_thenReturnUsers() throws Exception {	
		UserResponse user2 = new UserResponse()
				.email("real@real.com")
				.userRole(UserRole.USER);
		
		PageResponse<UserResponse> page = PageResponse.handleResponse(List.of(user1, user2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, true, true);

		given(userService.findAll(any(PaginationUtil.class))).willReturn(page);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		ResultActions action = mockMvc.perform(get("/api/v1/users")
										.params(params)
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(page.getContent().size())));
	}
	
	// TODO: UnauthorizedUserException 예외 클래스로 처리하는 방법.
	@DisplayName("사용자 목록을 조회하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPagination_whenCallGetUsers_thenThrowUnauthorizedUserException() throws Exception {	
		UserResponse user2 = new UserResponse()
				.email("real@real.com")
				.userRole(UserRole.USER);
		
		PageResponse<UserResponse> page = PageResponse.handleResponse(List.of(user1, user2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, true, true);

		given(userService.findAll(any(PaginationUtil.class))).willReturn(page);

		ResultActions action = mockMvc.perform(get("/api/v1/users")
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isForbidden());
	}
}