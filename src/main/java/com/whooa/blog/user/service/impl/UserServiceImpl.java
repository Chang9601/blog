package com.whooa.blog.user.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.whooa.blog.util.UserRoleMapper;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository userRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@Override
	public UserResponse create(UserCreateRequest userCreate) {
		if (userRepository.existsByEmail(userCreate.getEmail())) {
			throw new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."});
		}
		
		String userRole = userCreate.getUserRole();
		UserEntity userEntity = UserMapper.INSTANCE.toEntity(userCreate);
		
		System.out.println(userRole);
		
		String plainPassword = userEntity.getPassword();
		String hashedPassword = bCryptPasswordEncoder.encode(plainPassword);
		
		userEntity.setPassword(hashedPassword);
		userEntity.setUserRole(UserRoleMapper.map(userRole));
		
		return UserMapper.INSTANCE.toDto(userRepository.save(userEntity));
	}
	
	@Override
	public UserResponse find(UserDetailsImpl userDetailsImpl) {
		Long id = userDetailsImpl.getId();		
		UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.toDto(userEntity);

	}

	@Override
	public UserResponse findByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"이메일에 해당하는 사용자가 존재하지 않습니다."}));
		
		return UserMapper.INSTANCE.toDto(userEntity);
	}
}