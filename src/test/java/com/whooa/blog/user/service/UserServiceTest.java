package com.whooa.blog.user.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
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
	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	@Mock
	private UserRepository userRepository;
	
	private UserEntity userEntity;

	private UserCreateRequest userCreate;
	private UserResponse user;
		
	private UserDetailsImpl userDetailsImpl;

	@BeforeAll
	public void setUp() {
		userCreate = new UserCreateRequest();
		userCreate.setEmail("user1@naver.com");
		userCreate.setName("사용자1");
		userCreate.setPassword("12345678Aa!@#$%");
		userCreate.setUserRole("USER");
		
		userEntity = new UserEntity();
		userEntity.setEmail(userCreate.getEmail());
		userEntity.setName(userCreate.getName());
		userEntity.setPassword(userCreate.getPassword());
		userEntity.setUserRole(UserRole.USER);
	
		userDetailsImpl = new UserDetailsImpl(userEntity);
	}
	
	@DisplayName("회원가입에 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenReturnUser() {
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);
		given(userRepository.existsByEmail(any(String.class))).willReturn(false);
		
		user = userServiceImpl.create(userCreate);
				
		assertEquals(userCreate.getEmail(), user.getEmail());

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
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity);
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity));

		userServiceImpl.delete(userDetailsImpl);
		
		assertFalse(userEntity.getActive());
	
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
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity));
		
		user = userServiceImpl.find(userDetailsImpl);
		
		assertEquals(userEntity.getId(), user.getId());
		
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