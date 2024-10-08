package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.admin.service.impl.AdminUserServiceImpl;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminUserServiceTest {
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private AdminUserServiceImpl adminUserServiceImpl;
	
	private UserEntity userEntity1;

	private UserAdminUpdateRequest userAdminUpdate;
	private UserResponse user;
	
	@BeforeAll
	public void setUp() {
		userAdminUpdate = new UserAdminUpdateRequest();
		userAdminUpdate.setEmail("user2@naver.com");
		userAdminUpdate.setName("사용자2");
		userAdminUpdate.setPassword("12345678Aa!@#$%");
		userAdminUpdate.setUserRole("MANAGER");
		
		userEntity1 = new UserEntity();
		userEntity1.setEmail("user1@naver.com");
		userEntity1.setName("사용자1");
		userEntity1.setPassword("12345678Aa!@#$%");
		userEntity1.setUserRole(UserRole.USER);
	}
	
	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	public void givenNothing_whenCallDelete_thenReturnNothing() {
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity1);
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		adminUserServiceImpl.delete(userEntity1.getId());
		
		assertFalse(userEntity1.getActive());
	
		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
	
	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFind_thenReturnUser() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		user = adminUserServiceImpl.find(userEntity1.getId());
		
		assertEquals(userEntity1.getId(), user.getId());

		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}

	@DisplayName("사용자를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFind_thenThrowUserNotFoundException() {
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			adminUserServiceImpl.find(userEntity1.getId());
		});
	
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
	
	@DisplayName("사용자 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnUsers() {
		UserEntity userEntity2;
		
		userEntity2 = new UserEntity();
		userEntity2.setEmail("user2@naver.com");
		userEntity2.setName("사용자2");
		userEntity2.setPassword("12345678Aa!@#$%");
		userEntity2.setUserRole(UserRole.USER);

		given(userRepository.findByActiveTrue(any(Pageable.class))).willReturn(new PageImpl<UserEntity>(List.of(userEntity1, userEntity2)));

		PageResponse<UserResponse> page = adminUserServiceImpl.findAll(new PaginationUtil());
					
		assertEquals(2, page.getTotalElements());
		
		then(userRepository).should(times(1)).findByActiveTrue(any(Pageable.class));
	}
	
	@DisplayName("사용자를 수정하는데 성공한다.")
	@Test
	public void givenUserAdminUpdate_whenCallFindAll_thenReturnUsers() {
		UserEntity userEntity2;
	
		userEntity2 = new UserEntity();
		userEntity2.setEmail("user2@naver.com");
		userEntity2.setName("사용자2");
		userEntity2.setPassword("12345678Aa!@#$%");
		userEntity2.setUserRole(UserRole.MANAGER);
		
		given(userRepository.save(any(UserEntity.class))).willReturn(userEntity2);
		given(userRepository.existsByEmail(any(String.class))).willReturn(false);
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));

		user = adminUserServiceImpl.update(userEntity1.getId(), userAdminUpdate);
		
		assertEquals(userAdminUpdate.getEmail(), user.getEmail());
		assertEquals(UserRole.MANAGER, user.getUserRole());
				
		then(userRepository).should(times(1)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}
	
	@DisplayName("이메일을 사용하는 사용자가 이미 존재하여 수정하는데 실패한다.")
	@Test
	public void givenUserAdminUpdate_whenCallFindAll_thenThrowDuplicateUserException() {		
		given(userRepository.existsByEmail(any(String.class))).willReturn(true);
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.of(userEntity1));

		assertThrows(DuplicateUserException.class, () -> {
			adminUserServiceImpl.update(userEntity1.getId(), userAdminUpdate); 
		});
					
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(1)).existsByEmail(any(String.class));
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}	
	
	@DisplayName("사용자가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenUserAdminUpdate_whenCallFindAll_thenThrowUserNotFoundException() {		
		given(userRepository.findByIdAndActiveTrue(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(UserNotFoundException.class, () -> {
			adminUserServiceImpl.update(userEntity1.getId(), userAdminUpdate);
		});
						
		then(userRepository).should(times(0)).save(any(UserEntity.class));
		then(userRepository).should(times(0)).existsByEmail(any(String.class));
		then(userRepository).should(times(1)).findByIdAndActiveTrue(any(Long.class));
	}	
}