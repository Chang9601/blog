package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.service.impl.UserServiceImpl;
import com.whooa.blog.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	private UserCreateRequest userCreate;
	private UserEntity userEntity;
	
	@BeforeEach
	public void setUp() {
		String email = "test@test.com";
		String name = "테스트 이름";
		String password = "1234";
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
	}
	
	@DisplayName("사용자를 생성하는데 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenReturnUser() {
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);
		given(userRepository.existsByEmail(any(String.class))).willReturn(false);
		given(bCryptPasswordEncoder.encode(any(CharSequence.class))).willReturn("12sfar3131rafsa");
		
		UserResponse user = userServiceImpl.create(userCreate);
				
		assertNotNull(user);
		assertEquals(user.getEmail(), userCreate.getEmail());

		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
		then(bCryptPasswordEncoder).should(times(1)).encode(any(CharSequence.class));
	}
	
	@DisplayName("사용자 이미 존재하여 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenReturnDuplicateUserException() {
		given(userRepository.existsByEmail(any(String.class))).willReturn(true);
		
		assertThrows(DuplicateUserException.class, () -> {
			userServiceImpl.create(userCreate);
		});
		
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
		then(bCryptPasswordEncoder).should(times(0)).encode(any(CharSequence.class));
	}	
}