package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.service.impl.UserServiceImpl;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.PasswordUtil;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordUtil passwordUtil;
	
	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	private UserEntity userEntity1;

	private UserCreateRequest userCreate;
	private UserResponse user;
	
	private PaginationUtil pagination;
	
	private UserDetailsImpl userDetailsImpl;

	@BeforeAll
	public void setUpAll() {
		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	public void setUpEach() {
		String email = "test@test.com";
		String name = "테스트 이름";
		String password = "1234";
		String userRole = "USER";
		
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
		
		pagination = new PaginationUtil();
		
		userDetailsImpl = new UserDetailsImpl(userEntity1);
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities()));
		SecurityContextHolder.setContext(securityContext);
	}
	
	@DisplayName("사용자를 생성하는데 성공한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenReturnUser() {
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity1);
		given(userRepository.existsByEmail(any(String.class))).willReturn(false);
		given(passwordUtil.hash(any(String.class))).willReturn("12sfar3131rafsa");
		
		user = userServiceImpl.create(userCreate);
				
		assertEquals(user.getEmail(), userCreate.getEmail());

		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
		then(passwordUtil).should(times(1)).hash(any(String.class));
	}
	
	@DisplayName("사용자가 이미 존재하여 생성하는데 실패한다.")
	@Test
	public void givenUserCreate_whenCallCreate_thenThrowDuplicateUserException() {
		given(userRepository.existsByEmail(any(String.class))).willReturn(true);
		
		assertThrows(DuplicateUserException.class, () -> {
			userServiceImpl.create(userCreate);
		});
		
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
		then(passwordUtil).should(times(0)).hash(any(String.class));
	}
	
	@DisplayName("사용자를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			userServiceImpl.create(null);
		});
		
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(0)).existsByEmail(any(String.class));
		then(passwordUtil).should(times(0)).hash(any(String.class));
	}
	
	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	public void givenNothing_whenCallDelete_thenReturnNothing() {
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity1);
		
		userServiceImpl.delete(userDetailsImpl);
		
		assertFalse(userEntity1.getActive());
	
		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("사용자가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenNothing_whenCallDelete_thenThrowUserNotFoundException() {
		given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.delete(userDetailsImpl);			
		});

		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
	}
		
	@DisplayName("사용자를 조회(인증)하는데 성공한다.")
	@Test
	public void givenId_whenCallFind_thenReturnUser() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		user = userServiceImpl.find(userDetailsImpl);
		
		assertEquals(user.getId(), userEntity1.getId());
		
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}

	@DisplayName("사용자가 비활성 상태라서 조회(인증)하는데 실패한다.")
	@Test
	public void givenId_whenCallFind_thenThrowUserNotFoundException() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.find(userDetailsImpl);
		});
	
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
	
	@DisplayName("사용자 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnUsers() {
		UserEntity userEntity2 = new UserEntity()
				.email("real@real.com")
				.name("실전 이름")
				.password("4321")
				.userRole(UserRole.USER);
		
		given(userRepository.findByActiveTrue(any(Pageable.class))).willReturn(new PageImpl<UserEntity>(List.of(userEntity1, userEntity2)));

		PageResponse<UserResponse> page = userServiceImpl.findAll(pagination);
					
		assertEquals(page.getTotalElements(), 2);
		
		then(userRepository).should(times(1)).findByActiveTrue(any(Pageable.class));
	}
	
	@DisplayName("사용자를 조회(이메일)하는데 성공한다.")
	@Test
	public void givenId_whenCallFindByEmail_thenReturnUser() {
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(userEntity1));
		
		user = userServiceImpl.findByEmail(userCreate.getEmail());
		
		assertEquals(user.getId(), userEntity1.getId());

		then(userRepository).should(times(1)).findByEmail(any(String.class));
	}

	@DisplayName("사용자를 조회(이메일)하는데 실패한다.")
	@Test
	public void givenId_whenCallFindByEmail_thenThrowUserNotFoundException() {
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.findByEmail("real@real.com");
		});
	
		then(userRepository).should(times(1)).findByEmail(any(String.class));
	}
	
	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindById_thenReturnUser() {
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		user = userServiceImpl.findById(userEntity1.getId());
		
		assertEquals(user.getId(), userEntity1.getId());

		then(userRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("사용자를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindById_thenThrowUserNotFoundException() {
		given(userRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.findById(userEntity1.getId());
		});
	
		then(userRepository).should(times(1)).findById(any(Long.class));
	}	
}