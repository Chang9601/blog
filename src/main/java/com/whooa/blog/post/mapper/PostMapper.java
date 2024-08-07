package com.whooa.blog.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.whooa.blog.post.dto.PostDTO.PostCreateRequest;
import com.whooa.blog.post.dto.PostDTO.PostResponse;
import com.whooa.blog.post.entity.PostEntity;

@Mapper
public interface PostMapper {
	PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
	
	public abstract PostResponse toDto(PostEntity postEntity);
	public abstract PostEntity toEntity(PostCreateRequest postCreate);
}