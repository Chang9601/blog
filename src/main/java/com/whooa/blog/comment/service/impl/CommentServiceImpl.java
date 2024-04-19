package com.whooa.blog.comment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.dto.CommentDto.CreateRequest;
import com.whooa.blog.comment.dto.CommentDto.Response;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.utils.NotNullNotEmptyChecker;

@Service
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	public CommentServiceImpl(final CommentRepository commentRepository, final PostRepository postRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
	}
	
	@Override
	public CommentDto.Response create(final Long postId, final CommentDto.CreateRequest commentDto) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"포스트가 존재하지 않습니다."}));
		
		CommentEntity commentEntity = CommentMapper.INSTANCE.toEntity(commentDto);
		commentEntity.setPost(postEntity);
						
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}

	@Override
	public List<CommentDto.Response> findAllByPostId(final Long postId) {
		postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"포스트가 존재하지 않습니다."}));

		List<CommentEntity> commentEntities = commentRepository.findByPostId(postId);
		
		return commentEntities.stream().map((commentEntity) -> CommentMapper.INSTANCE.toDto(commentEntity)).collect(Collectors.toList());
	}

	@Override
	public CommentDto.Response update(final Long postId, final Long commentId, final CommentDto.UpdateRequest commentDto) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST.getCode(), Code.COMMENT_NOT_IN_POST.getMessage(), new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		String content = commentDto.getContent();
		
		if (NotNullNotEmptyChecker.check(content)) {
			commentEntity.setContent(content);
		}
		
		return  CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}

	@Override
	public void delete(Long postId, Long commentId) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST.getCode(), Code.COMMENT_NOT_IN_POST.getMessage(), new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		commentRepository.delete(commentEntity);
	}

	@Override
	public Response reply(Long postId, Long commentId, CreateRequest commentDto) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity parentCommentEntity = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!parentCommentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST.getCode(), Code.COMMENT_NOT_IN_POST.getMessage(), new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		CommentEntity commentEntity = CommentMapper.INSTANCE.toEntity(commentDto);
		commentEntity.setParentId(parentCommentEntity.getId());
		commentEntity.setPost(postEntity);
		
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}
}