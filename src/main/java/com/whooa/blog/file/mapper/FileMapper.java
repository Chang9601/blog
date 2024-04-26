package com.whooa.blog.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.whooa.blog.file.dto.FileDto;
import com.whooa.blog.file.entity.FileEntity;


@Mapper
public interface FileMapper {
	FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);
	
	public abstract FileDto toDto(FileEntity fileEntity);
	public abstract FileEntity toEntity(FileDto fileDto);
}
