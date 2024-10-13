package com.whooa.blog.post.mapper;

import com.whooa.blog.category.dto.CategoryDto;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-13T22:25:49+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.6.jar, environment: Java 17 (Oracle Corporation)"
)
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDto.PostResponse fromEntity(PostEntity postEntity) {
        if ( postEntity == null ) {
            return null;
        }

        PostDto.PostResponse postResponse = new PostDto.PostResponse();

        postResponse.setId( postEntity.getId() );
        postResponse.setContent( postEntity.getContent() );
        postResponse.setTitle( postEntity.getTitle() );
        postResponse.setCategory( categoryEntityToCategoryResponse( postEntity.getCategory() ) );
        postResponse.setComments( commentEntityListToCommentResponseList( postEntity.getComments() ) );
        List<File> list1 = postEntity.getFiles();
        if ( list1 != null ) {
            postResponse.setFiles( new ArrayList<File>( list1 ) );
        }

        return postResponse;
    }

    @Override
    public PostEntity toEntity(PostDto.PostCreateRequest postCreate) {
        if ( postCreate == null ) {
            return null;
        }

        PostEntity postEntity = new PostEntity();

        postEntity.setContent( postCreate.getContent() );
        postEntity.setTitle( postCreate.getTitle() );

        return postEntity;
    }

    @Override
    public PostDoc toDoc(PostEntity postEntity) {
        if ( postEntity == null ) {
            return null;
        }

        PostDoc postDoc = new PostDoc();

        postDoc.setId( postEntity.getId() );
        postDoc.setContent( postEntity.getContent() );
        postDoc.setTitle( postEntity.getTitle() );

        return postDoc;
    }

    protected CategoryDto.CategoryResponse categoryEntityToCategoryResponse(CategoryEntity categoryEntity) {
        if ( categoryEntity == null ) {
            return null;
        }

        CategoryDto.CategoryResponse categoryResponse = new CategoryDto.CategoryResponse();

        categoryResponse.setId( categoryEntity.getId() );
        categoryResponse.setName( categoryEntity.getName() );

        return categoryResponse;
    }

    protected CommentDto.CommentResponse commentEntityToCommentResponse(CommentEntity commentEntity) {
        if ( commentEntity == null ) {
            return null;
        }

        CommentDto.CommentResponse commentResponse = new CommentDto.CommentResponse();

        commentResponse.setId( commentEntity.getId() );
        commentResponse.setContent( commentEntity.getContent() );
        commentResponse.setParentId( commentEntity.getParentId() );

        return commentResponse;
    }

    protected List<CommentDto.CommentResponse> commentEntityListToCommentResponseList(List<CommentEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CommentDto.CommentResponse> list1 = new ArrayList<CommentDto.CommentResponse>( list.size() );
        for ( CommentEntity commentEntity : list ) {
            list1.add( commentEntityToCommentResponse( commentEntity ) );
        }

        return list1;
    }
}
