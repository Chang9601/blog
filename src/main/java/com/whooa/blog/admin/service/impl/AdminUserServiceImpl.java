package com.whooa.blog.admin.service.impl;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.admin.service.AdminUserService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.mapper.UserMapper;
import com.whooa.blog.user.mapper.UserRoleMapper;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.PasswordUtil;
import com.whooa.blog.util.StringUtil;

@Service
public class AdminUserServiceImpl implements AdminUserService {
	private UserRepository userRepository;

	public AdminUserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public void delete(Long id) {
		UserEntity userEntity;
		
		userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		userEntity.setActive(false);
		
		userRepository.save(userEntity);
	}

	@Override
	public UserResponse find(Long id) {
		UserEntity userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.fromEntity(userEntity);
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
				
		userResponse = userEntities.stream().map((userEntity) -> UserMapper.INSTANCE.fromEntity(userEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(userResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public UserResponse update(Long id, UserAdminUpdateRequest userAdminUpdate) {
		String email, name, password, userRole;
		UserEntity userEntity;
		
		userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));

		email = userAdminUpdate.getEmail();
		name = userAdminUpdate.getName();
		password = userAdminUpdate.getPassword();
		userRole = userAdminUpdate.getUserRole();
		
		if (StringUtil.notEmpty(email)) {
			if (userRepository.existsByEmail(email)) {
				throw new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 이미 존재합니다."});
			}
			
			userEntity.setEmail(email);
		}
		
		if (StringUtil.notEmpty(name)) {
			userEntity.setName(name);
		}
		
		if (StringUtil.notEmpty(password)) {
			userEntity.setPassword(PasswordUtil.hash(password));
		}
		
		if (!StringUtil.notEmpty(userRole)) {
			userEntity.setUserRole(UserRoleMapper.map(userRole));
		}
		
		return UserMapper.INSTANCE.fromEntity(userRepository.save(userEntity));
	}
}