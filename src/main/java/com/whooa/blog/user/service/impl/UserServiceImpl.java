package com.whooa.blog.user.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.mapper.UserMapper;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.service.UserService;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.PasswordUtil;
import com.whooa.blog.util.UserRoleMapper;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository userRepository;
	private PasswordUtil passwordUtil;
	
	public UserServiceImpl(UserRepository userRepository, PasswordUtil passwordUtil) {
		this.userRepository = userRepository;
		this.passwordUtil = passwordUtil;
	}
	
	@Override
	public UserResponse create(UserCreateRequest userCreate) {
		if (userRepository.existsByEmail(userCreate.getEmail())) {
			throw new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."});
		}
		
		String userRole = userCreate.getUserRole();
		UserEntity userEntity = UserMapper.INSTANCE.toEntity(userCreate);
				
		String plainPassword = userEntity.getPassword();
		String hashedPassword = passwordUtil.hash(plainPassword);
		
		userEntity.password(hashedPassword).userRole(UserRoleMapper.map(userRole));
		
		return UserMapper.INSTANCE.toDto(userRepository.save(userEntity));
	}
	
	@Override
	public void delete(UserDetailsImpl userDetailsImpl) {
		Long id = userDetailsImpl.getId();

		UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		userEntity.active(false);
		
		userRepository.save(userEntity);
	}
	
	@Override
	public UserResponse find(UserDetailsImpl userDetailsImpl) {
		Long id = userDetailsImpl.getId();		
		UserEntity userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.toDto(userEntity);
	}
	
	@Override
	public PageResponse<UserResponse> findAll(PaginationUtil paginationUtil) {
		Pageable pageable = paginationUtil.makePageable();
		
		Page<UserEntity> users = userRepository.findByActiveTrue(pageable);
		
		List<UserEntity> userEntities = users.getContent();
		int pageSize = users.getSize();
		int pageNo = users.getNumber();
		long totalElements = users.getTotalElements();
		int totalPages = users.getTotalPages();
		boolean isLast = users.isLast();
		boolean isFirst = users.isFirst();
				
		List<UserResponse> userResponse = userEntities.stream().map((userEntity) -> UserMapper.INSTANCE.toDto(userEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(userResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public UserResponse findByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"이메일에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.toDto(userEntity);
	}
	
	@Override
	public UserResponse findById(Long id) {
		UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.toDto(userEntity);
	}
	
	@Override
	public UserResponse update(UserCreateRequest userCreate) {
		// TODO Auto-generated method stub
		return null;
	}
}