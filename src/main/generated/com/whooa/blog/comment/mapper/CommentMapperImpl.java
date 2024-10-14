package com.whooa.blog.comment.mapper;

import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.entity.CommentEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-14T20:21:37+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.6.jar, environment: Java 17 (Oracle Corporation)"
)
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentDto.CommentResponse fromEntity(CommentEntity commentEntity) {
        if ( commentEntity == null ) {
            return null;
        }

        CommentDto.CommentResponse commentResponse = new CommentDto.CommentResponse();

        commentResponse.setId( commentEntity.getId() );
        commentResponse.setContent( commentEntity.getContent() );
        commentResponse.setParentId( commentEntity.getParentId() );

        return commentResponse;
    }

    @Override
    public CommentEntity toEntity(CommentDto.CommentCreateRequest commentCreate) {
        if ( commentCreate == null ) {
            return null;
        }

        CommentEntity commentEntity = new CommentEntity();

        commentEntity.setContent( commentCreate.getContent() );

        return commentEntity;
    }
}
