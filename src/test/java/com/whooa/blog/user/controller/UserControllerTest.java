package com.whooa.blog.user.controller;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.config.TestSecurityConfig;
import com.whooa.blog.config.WithMockCustomUser;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserPasswordUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserUpdateRequest;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.InvalidCredentialsException;
import com.whooa.blog.user.exception.SamePasswordException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.mapper.UserMapper;
import com.whooa.blog.user.service.UserService;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PasswordUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

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
	private UserUpdateRequest userUpdate;
	private UserPasswordUpdateRequest userPasswordUpdate;
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
		userCreate = new UserCreateRequest();
		userCreate.setEmail("user1@naver.com");
		userCreate.setName("사용자1");
		userCreate.setPassword("12345678Aa!@#$%");
		userCreate.setUserRole("USER");

		userUpdate = new UserUpdateRequest();
		userUpdate.setEmail("user2@naver.com");
		userUpdate.setName("사용자2");

		userPasswordUpdate = new UserPasswordUpdateRequest();
		userPasswordUpdate.setOldPassword("12345678Aa!@#$%");
		userPasswordUpdate.setNewPassword("12345679Aa!@#$%");
		
		userEntity = new UserEntity();
		userEntity.setEmail(userCreate.getEmail());
		userEntity.setName(userCreate.getName());
		userEntity.setPassword(userCreate.getPassword());
		userEntity.setUserRole(UserRole.USER);

		user1 = new UserResponse();
		user1.setId(userEntity.getId());
		user1.setEmail(userEntity.getEmail());
		user1.setUserRole(userEntity.getUserRole());
	}
	
	@DisplayName("회원가입에 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenReturnUser() throws Exception {
		ResultActions action;
		
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			return user1;
		});

		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.email", is(user1.getEmail())))
			.andExpect(jsonPath("$.data.userRole", is(user1.getUserRole().getRole())));
	}
	
	@DisplayName("이름이 짧아 회원가입에 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenThrowBadRequestExceptionForName() throws Exception {
		ResultActions action;
		
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {			
			return user1;
		});

		userCreate.name("사");
		
		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@DisplayName("이메일이 유효하지 않아 회원가입에 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenThrowBadRequestExceptionForEmail() throws Exception {
		ResultActions action;
		
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});

		userCreate.email("user1naver.com");
		
		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("비밀번호가 유효하지 않아 회원가입에 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenThrowBadRequestExceptionForPassword() throws Exception {
		ResultActions action;
		
		given(userService.create(any(UserCreateRequest.class))).willAnswer((answer) -> {
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});

		userCreate.password("12314241");
		
		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("사용자가 이미 존재하여 회원가입에 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenThrowDuplicateUserException() throws Exception {
		ResultActions action;
		
		given(userService.create(any(UserCreateRequest.class))).willThrow(new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."}));

		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isConflict())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateUserException));
	}
	
	@DisplayName("회원탈퇴에 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenUserDetailsImpl_whenCallDeleteMe_thenReturnNothing() throws Exception {
		ResultActions action;
		
		willDoNothing().given(userService).delete(any(UserDetailsImpl.class));

		action = mockMvc.perform(delete("/api/v1/users"));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("사용자가 존재하지 않아 회원탈퇴에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserDetailsImpl_whenCallDeleteMe_thenThrowUserNotFoundException() throws Exception {		
		ResultActions action;
		
		willThrow(new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."})).given(userService).delete(any(UserDetailsImpl.class));
		
		action = mockMvc.perform(delete("/api/v1/users"));
		
		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
	}
	
	// TODO: UnauthenticatedUserException 예외 클래스로 처리하는 방법.
	@DisplayName("인증되어 있지 않아 회원탈퇴에 실패한다.")
	@Test
	public void givenUserDetailsImpl_whenCallDeleteMe_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		willDoNothing().given(userService).delete(any(UserDetailsImpl.class));

		action = mockMvc.perform(delete("/api/v1/users"));
		
		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("회원조회에 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenUserDetailsImpl_whenCallGetMe_thenReturnUser() throws Exception {
		ResultActions action;
		
		given(userService.find(any(UserDetailsImpl.class))).willReturn(user1);

		action = mockMvc.perform(
			get("/api/v1/users")
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email", is(user1.getEmail())));
	}
	
	@DisplayName("사용자가 존재하지 않아 회원조회에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserDetailsImpl_whenCallGetMe_thenThrowUserNotFoundException() throws Exception {
		ResultActions action;
		
		given(userService.find(any(UserDetailsImpl.class))).willThrow(new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));

		action = mockMvc.perform(
			get("/api/v1/users")
			.characterEncoding(StandardCharsets.UTF_8)
		);

		action
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
	}	
	
	@DisplayName("인증되어 있지 않아 회원조회에 실패한다.")
	@Test
	public void givenUserDetailsImpl_whenCallGetMe_thenThrowUnauthenticatedUserException() throws Exception {		
		ResultActions action;
		
		given(userService.find(any(UserDetailsImpl.class))).willReturn(user1);

		action = mockMvc.perform(
			get("/api/v1/users")
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("회원수정에 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenUserUpdate_whenCallUpdateMe_thenReturnUser() throws Exception {
		ResultActions action;
		
		given(userService.update(any(UserUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setEmail(userUpdate.getEmail());
			
			user1.setEmail(userEntity.getEmail());
						
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});
		
		action = mockMvc.perform(
			patch("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email", is(user1.getEmail())));
	}
	
	
	@DisplayName("이름이 짧아 회원수정에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserUpdate_whenCallUpdateMe_thenThrowBadRequestExceptionForName() throws Exception {
		ResultActions action;
		
		given(userService.update(any(UserUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setEmail(userUpdate.getEmail());
			
			user1.setEmail(userEntity.getEmail());
			
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});

		userUpdate.setName("사");
		
		action = mockMvc.perform(
			patch("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@DisplayName("이메일이 유효하지 않아 회원수정에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserUpdate_whenCallUpdateMe_thenThrowBadRequestExceptionForEmail() throws Exception {
		ResultActions action;
		
		given(userService.update(any(UserUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setEmail(userUpdate.getEmail());
			
			user1.setEmail(userEntity.getEmail());
			
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});

		userUpdate.setEmail("user1naver.com");
		
		action = mockMvc.perform(
			patch("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@DisplayName("사용자 이미 존재하여 회원수정에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserUpdate_whenCallUpdateMe_thenThrowDuplicateUserException() throws Exception {
		ResultActions action;
		
		given(userService.update(any(UserUpdateRequest.class), any(UserDetailsImpl.class))).willThrow(new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."}));

		action = mockMvc.perform(
			patch("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isConflict())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateUserException));
	}
	
	@DisplayName("인증되어 있지 않아 회원수정에 실패한다.")
	@Test
	public void givenUserUpdate_whenCallUpdateMe_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		given(userService.update(any(UserUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setEmail(userUpdate.getEmail());
			
			user1.setEmail(userEntity.getEmail());
			
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});
		
		action = mockMvc.perform(
			patch("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("비밀번호 수정에 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenReturnUser() throws Exception {
		ResultActions action;
		
		given(userService.updatePassowrd(any(UserPasswordUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setPassword(PasswordUtil.hash(userPasswordUpdate.getNewPassword()));
						
			return UserMapper.INSTANCE.fromEntity(userEntity);
		});
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("새 비밀번호가 유효하지 않아 비밀번호 수정에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowBadRequestExceptionForPassword() throws Exception {
		ResultActions action;
		
		given(userService.updatePassowrd(any(UserPasswordUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setPassword(PasswordUtil.hash(userPasswordUpdate.getNewPassword()));
			
			return user1;
		});
		
		userPasswordUpdate.setNewPassword("12314241");
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("비밀번호가 정확하지 않아 비밀번호 수정에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowInvalidCredentialsException() throws Exception {
		ResultActions action;
		
		given(userService.updatePassowrd(any(UserPasswordUpdateRequest.class), any(UserDetailsImpl.class))).willThrow(new InvalidCredentialsException(Code.BAD_REQUEST, new String[] {"비밀번호가 정확하지 않습니다."}));
		
		userPasswordUpdate.setOldPassword("12345678bB!@#$%");
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
				
		action
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCredentialsException));
	}
	
	@DisplayName("새 비밀번호와 구 비밀번호가 일치하여 비밀번호 수정에 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowSamePasswordException() throws Exception {
		ResultActions action;
		
		given(userService.updatePassowrd(any(UserPasswordUpdateRequest.class), any(UserDetailsImpl.class))).willThrow(new SamePasswordException(Code.BAD_REQUEST, new String[] {"새 비밀번호는 현재 비밀번호와 달라야 합니다."}));
		
		userPasswordUpdate.setNewPassword(userPasswordUpdate.getOldPassword());
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertTrue(result.getResolvedException() instanceof SamePasswordException));
	}
	
	@DisplayName("인증되어 있지 않아 비밀번호 수정에 실패한다.")
	@Test
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		given(userService.updatePassowrd(any(UserPasswordUpdateRequest.class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			userEntity.setPassword(PasswordUtil.hash(userPasswordUpdate.getNewPassword()));

			return UserMapper.INSTANCE.fromEntity(userEntity);
		});
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);	

		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}	
}