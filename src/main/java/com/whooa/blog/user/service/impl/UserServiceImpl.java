package com.whooa.blog.user.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.mapper.UserMapper;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.service.UserService;
import com.whooa.blog.user.type.UserRole;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public UserResponse create(UserCreateRequest userCreate) {
		if (userRepository.existsByEmail(userCreate.getEmail())) {
			throw new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."});
		}
		
		UserEntity userEntity = UserMapper.INSTANCE.toEntity(userCreate);
		
		String password = userEntity.getPassword();
		String encodedPassword = passwordEncoder.encode(password);
		
		userEntity.setPassword(encodedPassword);
		userEntity.setUserRole(UserRole.USER);
		
		return UserMapper.INSTANCE.toDto(userRepository.save(userEntity));
	}

	@Override
	public UserResponse findByEmail(String email) {
		if (!userRepository.existsByEmail(email)) {
			throw new UserNotFoundException(Code.NOT_FOUND, new String[] {"이메일에 해당하는 사용자가 존재하지 않습니다."});
		}
		
		return UserMapper.INSTANCE.toDto(userRepository.findByEmail(email).get());
	}
}