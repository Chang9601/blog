package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.service.impl.UserServiceImpl;
import com.whooa.blog.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	private UserEntity userEntity1;

	private UserCreateRequest userCreate;
	private UserResponse user;
		
	private UserDetailsImpl userDetailsImpl;

	@BeforeEach
	public void setUpEach() {
		String email, name, password, userRole;
		
		email = "user1@user1.com";
		name = "사용자1";
		password = "12345678Aa!@#$%";
		userRole = "USER";
		
		userEntity1 = new UserEntity()
					.email(email)
					.name(name)
					.password(password)
					.userRole(UserRole.USER);
		
		userCreate = new UserCreateRequest()
					.email(email)
					.name(name)
					.password(password)
					.userRole(userRole);
				
		userDetailsImpl = new UserDetailsImpl(userEntity1);
	}
	
	@DisplayName("회원가입에 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenReturnUser() {
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity1);
		given(userRepository.existsByEmail(any(String.class))).willReturn(false);
		
		user = userServiceImpl.create(userCreate);
				
		assertEquals(user.getEmail(), userCreate.getEmail());

		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
	}
	
	@DisplayName("사용자가 이미 존재하여 회원가입에 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenThrowDuplicateUserException() {
		given(userRepository.existsByEmail(any(String.class))).willReturn(true);
		
		assertThrows(DuplicateUserException.class, () -> {
			userServiceImpl.create(userCreate);
		});
		
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
	}
	
	@DisplayName("회원가입에 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			userServiceImpl.create(null);
		});
		
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(0)).existsByEmail(any(String.class));
	}
	
	@DisplayName("회원탈퇴에 성공한다.")
	@Test
	public void givenNothing_whenCallDelete_thenReturnNothing() {
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity1);
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));

		userServiceImpl.delete(userDetailsImpl);
		
		assertFalse(userEntity1.getActive());
	
		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
	
	@DisplayName("사용자가 존재하지 않아 회원탈퇴에 실패한다.")
	@Test
	public void givenNothing_whenCallDelete_thenThrowUserNotFoundException() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.delete(userDetailsImpl);			
		});

		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
		
	@DisplayName("회원조회에 성공한다.")
	@Test
	public void givenUserDetailsImpl_whenCallFind_thenReturnUser() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		user = userServiceImpl.find(userDetailsImpl);
		
		assertEquals(user.getId(), userEntity1.getId());
		
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}

	@DisplayName("사용자가 존재하지 않아 회원조회에 실패한다.")
	@Test
	public void givenUserDetailsImpl_whenCallFind_thenThrowUserNotFoundException() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.find(userDetailsImpl);
		});
	
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
}