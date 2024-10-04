package com.whooa.blog.user.mapper;

import org.springframework.stereotype.Component;

import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.util.PasswordUtil;

@Component
public class UserMapper {
	
	public UserEntity toEntity(UserCreateRequest userCreate) {
		if (userCreate == null) {
			return null;
		}
		
		UserEntity userEntity = UserEntity.builder()
									.email(userCreate.getEmail())
									.name(userCreate.getName())
									.password(PasswordUtil.hash(userCreate.getPassword()))
									.userRole(UserRoleMapper.map(userCreate.getUserRole()))
									.build();
		
		return userEntity;
	}
	
	public UserResponse fromEntity(UserEntity userEntity) {
		if (userEntity == null) {
			return null;
		}
		
		UserResponse user = UserResponse.builder()
								.id(userEntity.getId())
								.email(userEntity.getEmail())
								.userRole(userEntity.getUserRole())
								.build();
		
		return user;
	}
}