package com.whooa.blog.user.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDTO.UserCreateRequest;
import com.whooa.blog.user.dto.UserDTO.UserResponse;
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
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserResponse create(UserCreateRequest userCreate) {
		String email, plainPassword, hashedPassword, userRole;
		UserEntity userEntity;
		
		email = userCreate.getEmail();
		
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."});
		}
		
		userRole = userCreate.getUserRole();
		userEntity = UserMapper.INSTANCE.toEntity(userCreate);
				
		plainPassword = userEntity.getPassword();
		hashedPassword = PasswordUtil.hash(plainPassword);
		
		userEntity.password(hashedPassword).userRole(UserRoleMapper.map(userRole));
		
		return UserMapper.INSTANCE.toDto(userRepository.save(userEntity));
	}
	
	@Override
	public void delete(UserDetailsImpl userDetailsImpl) {
		Long id;
		UserEntity userEntity;
		
		id = userDetailsImpl.getId();
		
		userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		userEntity.active(false);
		
		userRepository.save(userEntity);
	}
	
	@Override
	public UserResponse find(UserDetailsImpl userDetailsImpl) {
		Long id;
		UserEntity userEntity;	
		
		id = userDetailsImpl.getId();		
		userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.toDto(userEntity);
	}
	
	@Override
	public PageResponse<UserResponse> findAll(PaginationUtil paginationUtil) {
		Pageable pageable;
		Page<UserEntity> page;
		List<UserEntity> userEntities;
		List<UserResponse> userResponse;
		int pageSize, pageNo, totalPages;
		long totalElements;
		boolean isLast, isFirst;
		
		pageable = paginationUtil.makePageable();
		page = userRepository.findByActiveTrue(pageable);
		
		userEntities = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
				
		userResponse = userEntities.stream().map((userEntity) -> UserMapper.INSTANCE.toDto(userEntity)).collect(Collectors.toList());
		
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