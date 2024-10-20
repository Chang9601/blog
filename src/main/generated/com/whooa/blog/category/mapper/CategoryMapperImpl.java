package com.whooa.blog.category.mapper;

import com.whooa.blog.category.dto.CategoryDto;
import com.whooa.blog.category.entity.CategoryEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-20T16:05:01+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.6.jar, environment: Java 17 (Oracle Corporation)"
)
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto.CategoryResponse fromEntity(CategoryEntity categoryEntity) {
        if ( categoryEntity == null ) {
            return null;
        }

        CategoryDto.CategoryResponse categoryResponse = new CategoryDto.CategoryResponse();

        categoryResponse.setId( categoryEntity.getId() );
        categoryResponse.setName( categoryEntity.getName() );

        return categoryResponse;
    }

    @Override
    public CategoryEntity toEntity(CategoryDto.CategoryCreateRequest categoryCreate) {
        if ( categoryCreate == null ) {
            return null;
        }

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setName( categoryCreate.getName() );

        return categoryEntity;
    }
}
