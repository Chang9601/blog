package com.whooa.blog.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserPasswordUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserUpdateRequest;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.repository.UserRepository;
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
	
	private UserCreateRequest userCreate;
	private UserUpdateRequest userUpdate;
	private UserPasswordUpdateRequest userPasswordUpdate;

	private UserDetailsImpl userDetailsImpl;

	@BeforeAll
	void setUpAll() {
		mockMvc = MockMvcBuilders
					.webAppContextSetup(webApplicationContext)
					.addFilter(new CharacterEncodingFilter("utf-8", true))
					.apply(springSecurity()).build();
	}
	
	@BeforeEach
	void setUpEach() {
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
	}
	
	@AfterAll
	void tearDownAll() {
		userRepository.deleteAll();
	}
	
	@AfterEach
	void tearDownEach() {
		userRepository.deleteAll();
	}

	@DisplayName("회원가입에 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenReturnUser() throws Exception {
		ResultActions action;
		
		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);

		action
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.email", is(userCreate.getEmail())))
			.andExpect(jsonPath("$.data.userRole", is(userCreate.getUserRole())));
	}
	
	@DisplayName("이름이 짧아 회원가입에 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreateMe_thenThrowBadRequestExceptionForName() throws Exception {
		ResultActions action;
		
		userCreate.name("테");
		
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
		
		userCreate.email("testtest.com");
		
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
		
		userCreate.password("12341231");
		
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
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);
		
		action = mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isConflict())
			.andExpect(output -> assertTrue(output.getResolvedException() instanceof DuplicateUserException));
	}
	
	@DisplayName("회원탈퇴에 성공한다.")
	@Test
	public void givenId_whenCallDeleteMe_thenReturnNothing() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);
		
		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		action = mockMvc.perform(delete("/api/v1/users").with(user(userDetailsImpl)));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("인증되어 있지 않아 회원탈퇴에 실패한다.")
	@Test
	public void givenId_whenCallDeleteMe_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);
		
		action = mockMvc.perform(delete("/api/v1/users"));
		
		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
	
	@DisplayName("회원조회에 성공한다.")
	@Test
	public void givenNothing_whenCallGetMe_thenReturnUser() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		action = mockMvc.perform(
			get("/api/v1/users")
			.with(user(userDetailsImpl))
			.characterEncoding(StandardCharsets.UTF_8)
		);
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email", is(userCreate.getEmail())));
	}
	
	@DisplayName("인증되어 있지 않아 회원조회에 실패한다.")
	@Test
	public void givenNothing_whenCallGetMe_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
		
		action = mockMvc.perform(get("/api/v1/users").characterEncoding(StandardCharsets.UTF_8));
		
		action
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("회원수정에 성공한다.")
	@Test
	public void givenUserUpdate_whenCallUpdateMe_thenReturnUser() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		mockMvc.perform(
			patch("/api/v1/users")
			.with(user(userDetailsImpl))
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()
		);
		
		action = mockMvc.perform(get("/api/v1/users").with(user(userDetailsImpl)).characterEncoding(StandardCharsets.UTF_8));
		
		action
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email", is(userUpdate.getEmail())));
	}
	
	@DisplayName("이름이 짧아 회원수정에 실패한다.")
	@Test
	public void givenUserUpdate_whenCallUpdateMe_thenThrowBadRequestExceptionForName() throws Exception {
		ResultActions action;
		
		userUpdate.setName("사");
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		action = mockMvc.perform(
			patch("/api/v1/users")
			.with(user(userDetailsImpl))
			.content(SerializeDeserializeUtil.serializeToString(userUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON));

		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("이메일이 유효하지 않아 회원수정에 실패한다.")
	@Test
	public void givenUserUpdate_whenCallUpdateMe_thenThrowBadRequestExceptionForEmail() throws Exception {
		ResultActions action;
		
		userUpdate.setEmail("user1naver.com");

		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		action = mockMvc.perform(
			patch("/api/v1/users")
			.with(user(userDetailsImpl))
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
	public void givenUserUpdate_whenCallUpdateMe_thenThrowDuplicateUserException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);
		
		userCreate.email("user2@naver.com");
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		action = mockMvc.perform(
			patch("/api/v1/users")
			.with(user(userDetailsImpl))
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
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

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
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenReturnUser() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.with(user(userDetailsImpl))
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
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowBadRequestExceptionForPassword() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		userPasswordUpdate.setNewPassword("12312414");
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.with(user(userDetailsImpl))
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
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowInvalidCredentialsException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		userPasswordUpdate.setOldPassword("12345678bB!@#$%");
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.with(user(userDetailsImpl))
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("새 비밀번호와 구 비밀번호가 일치하여 비밀번호 수정에 실패한다.")
	@Test
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowSamePasswordException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmailAndActiveTrue(userCreate.getEmail()).get());

		userPasswordUpdate.setNewPassword(userPasswordUpdate.getOldPassword());
		
		action = mockMvc.perform(
			patch("/api/v1/users/update-my-password")
			.with(user(userDetailsImpl))
			.content(SerializeDeserializeUtil.serializeToString(userPasswordUpdate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON)
		);
		
		action
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
	
	@DisplayName("인증되어 있지 않아 비밀번호 수정에 실패한다.")
	@Test
	public void givenUserPasswordUpdate_whenCallUpdateMyPassword_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		
		mockMvc.perform(
			post("/api/v1/users")
			.with(user(userDetailsImpl))
			.content(SerializeDeserializeUtil.serializeToString(userCreate))
			.characterEncoding(StandardCharsets.UTF_8)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated()
		);

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
	// TODO: 의존성 오류.
//	@DisplayName("로그인 하는데 성공한다.")
//	@Test
//	public void givenEmailAndPassword_whenCallJsonUsernamePasswordAuthenticationFilter_thenReturnUser() throws Exception {
//		mockMvc.perform(post("/api/v1/users")
//				.content(SerializeDeserializeUtil.serializeToString(userCreate))
//				.characterEncoding(StandardCharsets.UTF_8)
//				.contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isCreated());
//
//		userDetailsImpl = new UserDetailsImpl(userRepository.findByEmail("user2@naver.com").get());
//		
//		Map<String, String> signInDto = new HashMap<>();
//		
//		signInDto.put("email", userCreate.getEmail());
//		signInDto.put("password", userCreate.getPassword());
//
//		ResultActions action = mockMvc.perform(post("/api/v1/auth/sign-in")
//										.with(user(userDetailsImpl))
//										.content(SerializeDeserializeUtil.serializeToString(signInDto))
//										.characterEncoding(StandardCharsets.UTF_8)
//										.contentType(MediaType.APPLICATION_JSON));		
//		action.andDo(print())
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.data.email", is(userCreate.getEmail())));
//	}
}