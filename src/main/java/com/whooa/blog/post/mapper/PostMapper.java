package com.whooa.blog.post.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.user.entity.UserEntity;

@Component
public class PostMapper {
	
	public PostEntity toEntity(PostCreateRequest postCreate, Object... entities) {
		CategoryEntity categoryEntity;
		UserEntity userEntity;
		
		if (postCreate == null) {
			return null;
		}
		
		if (entities.length > 0 && entities[0] instanceof PostEntity) {
			categoryEntity = (CategoryEntity) entities[0];
		} else {
			return null;
		}

		if (entities.length > 1 && entities[0] instanceof PostEntity) {
			userEntity = (UserEntity) entities[1];
		} else {
			return null;
		}
		
		PostEntity postEntity = PostEntity.builder()
									.content(postCreate.getContent())
									.title(postCreate.getTitle())
									.category(categoryEntity)
									.user(userEntity)
									.build();
		
		return postEntity;
	}
	
	public PostResponse fromEntity(PostEntity postEntity) {
		if (postEntity == null) {
			return null;
		}
		
		PostResponse post = PostResponse.builder()
								.content(postEntity.getContent())
								.title(postEntity.getTitle())
								.category(fromCategoryEntity(postEntity.getCategory()))
								.comments(fromCommentEntities(postEntity.getComments()))
								.files(postEntity.getFiles())
								.build();
		return post;
	}
	
	private CommentResponse fromCommentEntity(CommentEntity commentEntity) {
        if (commentEntity == null) {
            return null;
        }
        
        CommentResponse comment = CommentResponse.builder()
        								.id(commentEntity.getId())
        								.content(commentEntity.getContent())
        								.parentId(commentEntity.getParentId())
        								.build();
        
        return comment;
	}
	
	private List<CommentResponse> fromCommentEntities(List<CommentEntity> commentEntities) {
		List<CommentResponse> comments;
		
		if (commentEntities == null) {
			return null;
		}
		
		comments = new ArrayList<CommentResponse>(commentEntities.size());
		for (CommentEntity commentEntity : commentEntities) {
			comments.add(fromCommentEntity(commentEntity));
		}
		
		return comments;
	}
	
	private CategoryResponse fromCategoryEntity(CategoryEntity categoryEntity) {
        if (categoryEntity == null) {
            return null;
        }
        
        CategoryResponse category = CategoryResponse.builder()
        								.id(categoryEntity.getId())
        								.name(categoryEntity.getName())
        								.build();
        
        return category;
	}
}