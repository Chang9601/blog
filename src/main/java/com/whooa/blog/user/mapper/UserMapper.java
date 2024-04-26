package com.whooa.blog.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;

@Mapper
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	public abstract UserResponse toDto(UserEntity userEntity);
	public abstract UserEntity toEntity(UserCreateRequest userCreate);
}