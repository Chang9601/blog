package com.whooa.blog.category.mapper;

import org.springframework.stereotype.Component;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.entity.CategoryEntity;

@Component
public class CategoryMapper {

	public CategoryEntity toEntity(CategoryCreateRequest categoryCreate) {
		if (categoryCreate == null) {
			return null;
		}
		
		CategoryEntity categoryEntity = CategoryEntity.builder()
			.name(categoryCreate.getName())
			.build();	
		
		return categoryEntity;
	}
	
	public CategoryResponse fromEntity(CategoryEntity categoryEntity) {
        if (categoryEntity == null) {
            return null;
        }
        
        CategoryResponse categoryResponse = CategoryResponse.builder()
        	.id(categoryEntity.getId())
        	.name(categoryEntity.getName())
        	.build();
        
       return categoryResponse;	
	}
}
