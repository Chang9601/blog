package com.whooa.blog.comment.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;

import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.entity.CommentEntity;

@Mapper
public interface CommentMapper {
	CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
	
	CommentDto.Response toDto(final CommentEntity commentEntity);
	CommentEntity toEntity(final CommentDto.CreateRequest commentDto);
}
