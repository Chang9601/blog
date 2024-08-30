package com.whooa.blog.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;

@Mapper
public interface CommentMapper {
	CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
	
	public abstract CommentResponse fromEntity(CommentEntity commentEntity);
	public abstract CommentEntity toEntity(CommentCreateRequest commentCreate);
}