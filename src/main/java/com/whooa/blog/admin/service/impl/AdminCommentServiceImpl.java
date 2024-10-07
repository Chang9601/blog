package com.whooa.blog.admin.service.impl;

import org.springframework.stereotype.Service;

import com.whooa.blog.admin.service.AdminCommentService;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.util.StringUtil;

@Service
public class AdminCommentServiceImpl implements AdminCommentService {
	private CommentRepository commentRepository;
	private PostRepository postRepository;

	public AdminCommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
	}
	
	@Override
	public void delete(Long id, Long postId) {
		CommentEntity commentEntity;
		
		commentEntity = find(id, postId);
		
		commentRepository.delete(commentEntity);
	}

	@Override
	public CommentResponse update(Long id, Long postId, CommentUpdateRequest commentUpdate) {
		CommentEntity commentEntity;
		String content;

		commentEntity = find(id, postId);
		
		content = commentUpdate.getContent();
			
		if (StringUtil.notEmpty(content)) {
			commentEntity.setContent(content);
		}
		
		return CommentMapper.INSTANCE.fromEntity(commentRepository.save(commentEntity));
	}
	
	private CommentEntity find(Long id, Long postId) {
		CommentEntity commentEntity;
		PostEntity postEntity;
		
		postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		commentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		return commentEntity;
	}	
}