package com.whooa.blog.user.service.impl;

import org.springframework.stereotype.Service;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserPasswordUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserUpdateRequest;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.InvalidCredentialsException;
import com.whooa.blog.user.exception.SamePasswordException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.mapper.UserMapper;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.service.UserService;
import com.whooa.blog.util.PasswordUtil;
import com.whooa.blog.util.StringUtil;
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
		
		// TODO: 실제로 삭제하지 않고 active 필드만 false로 둘 경우 처리방법.
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
		
		userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
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
	public UserResponse update(UserUpdateRequest userUpdate, UserDetailsImpl userDetailsImpl) {
		String email, name;
		Long id;
		UserEntity userEntity;
		
		id = userDetailsImpl.getId();		
		userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		email = userUpdate.getEmail();
		name = userUpdate.getName();

		if (StringUtil.notEmpty(email)) {
			if (userRepository.existsByEmail(email)) {
				throw new DuplicateUserException(Code.CONFLICT, new String[] {"이메일을 사용하는 사용자가 존재합니다."});
			}
			
			userEntity.email(email);
		}
		
		if (StringUtil.notEmpty(name)) {
			userEntity.name(name);
		}
		
		return UserMapper.INSTANCE.toDto(userRepository.save(userEntity));
	}

	@Override
	public UserResponse updatePassowrd(UserPasswordUpdateRequest userPasswordUpdate, UserDetailsImpl userDetailsImpl) {
		String newPassword, oldPassword, password;
		Long id;
		UserEntity userEntity;
		
		id = userDetailsImpl.getId();		
		userEntity = userRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		newPassword = userPasswordUpdate.getNewPassword();
		oldPassword = userPasswordUpdate.getOldPassword();
		password = userEntity.getPassword();
		
		if (!PasswordUtil.match(oldPassword, password)) {
			throw new InvalidCredentialsException(Code.BAD_REQUEST, new String[] {"비밀번호가 정확하지 않습니다."});
		}
		
		if (oldPassword.equals(newPassword)) {
			throw new SamePasswordException(Code.BAD_REQUEST, new String[] {"새 비밀번호는 현재 비밀번호와 달라야 합니다."});
		}
		
		userEntity.password(PasswordUtil.hash(newPassword));

		return UserMapper.INSTANCE.toDto(userRepository.save(userEntity));
	}
}