package com.whooa.blog.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.Post;

@Mapper
public interface PostMapper {
	
	PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
	
	PostDto.Response toDto(Post post);
	Post toEntity(PostDto.Request postDto);
}
