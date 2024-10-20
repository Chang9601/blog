package com.whooa.blog.post.mapper;

import com.whooa.blog.category.dto.CategoryDto;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-20T19:39:48+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.6.jar, environment: Java 17 (Oracle Corporation)"
)
public class PostMapperImpl implements PostMapper {

    private final DatatypeFactory datatypeFactory;

    public PostMapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

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
        postDoc.setCreatedAt( xmlGregorianCalendarToLocalDate( localDateTimeToXmlGregorianCalendar( postEntity.getCreatedAt() ) ) );

        return postDoc;
    }

    @Override
    public PostDto.PostResponse fromDoc(PostDoc postDoc) {
        if ( postDoc == null ) {
            return null;
        }

        PostDto.PostResponse postResponse = new PostDto.PostResponse();

        postResponse.setId( postDoc.getId() );
        postResponse.setContent( postDoc.getContent() );
        postResponse.setTitle( postDoc.getTitle() );

        return postResponse;
    }

    private XMLGregorianCalendar localDateTimeToXmlGregorianCalendar( LocalDateTime localDateTime ) {
        if ( localDateTime == null ) {
            return null;
        }

        return datatypeFactory.newXMLGregorianCalendar(
            localDateTime.getYear(),
            localDateTime.getMonthValue(),
            localDateTime.getDayOfMonth(),
            localDateTime.getHour(),
            localDateTime.getMinute(),
            localDateTime.getSecond(),
            localDateTime.get( ChronoField.MILLI_OF_SECOND ),
            DatatypeConstants.FIELD_UNDEFINED );
    }

    private static LocalDate xmlGregorianCalendarToLocalDate( XMLGregorianCalendar xcal ) {
        if ( xcal == null ) {
            return null;
        }

        return LocalDate.of( xcal.getYear(), xcal.getMonth(), xcal.getDay() );
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
