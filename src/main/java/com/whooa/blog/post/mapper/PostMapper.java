package com.whooa.blog.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;

@Mapper
public interface PostMapper {
	
	PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
	
	PostDto.Response toDto(PostEntity postEntity);
	PostEntity toEntity(PostDto.Request postDto);
}
