package com.whooa.blog.comment.mapper;
import org.springframework.stereotype.Component;

import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.user.entity.UserEntity;

@Component
public class CommentMapper {
	
	public CommentEntity toEntity(CommentCreateRequest commentCreate, Object... entities) {
		PostEntity postEntity;
		UserEntity userEntity;
		
		if (commentCreate == null) {
			return null;
		}
		
		if (entities.length > 0 && entities[0] instanceof PostEntity) {
			postEntity = (PostEntity) entities[0];
		} else {
			return null;
		}

		if (entities.length > 1 && entities[0] instanceof PostEntity) {
			userEntity = (UserEntity) entities[1];
		} else {
			return null;
		}
		
		
		CommentEntity commentEntity = CommentEntity.builder()
										.content(commentCreate.getContent())
										.post(postEntity)
										.user(userEntity)
										.build();
		
		return commentEntity;
	}
	
	public CommentResponse fromEntity(CommentEntity commentEntity) {
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
}