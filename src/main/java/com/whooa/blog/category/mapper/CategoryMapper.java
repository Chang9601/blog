package com.whooa.blog.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.whooa.blog.category.dto.CategoryDTO.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDTO.CategoryResponse;
import com.whooa.blog.category.entity.CategoryEntity;

@Mapper
public interface CategoryMapper {
	CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

	public abstract CategoryResponse toDto(CategoryEntity categoryEntity);
	public abstract CategoryEntity toEntity(CategoryCreateRequest categoryCreate);
}
