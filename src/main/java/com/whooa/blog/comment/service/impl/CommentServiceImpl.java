package com.whooa.blog.comment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.comment.repository.CommentRepository;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.NotNullNotEmptyChecker;
import com.whooa.blog.util.PaginationUtil;

@Service
public class CommentServiceImpl implements CommentService {
	private CommentRepository commentRepository;
	private PostRepository postRepository;
	private UserRepository userRepository;

	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public CommentResponse create(Long postId, CommentCreateRequest commentCreate, UserDetailsImpl userDetailsImpl) {
		Long userId = userDetailsImpl.getId();

		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));	
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"사용자가 존재하지 않습니다."}));
		CommentEntity commentEntity = CommentMapper.INSTANCE.toEntity(commentCreate);
		
		commentEntity.post(postEntity).user(userEntity);
	
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}
	
	@Override
	public void delete(Long id, Long postId, UserDetailsImpl userDetailsImpl) {

		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity commentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		UserEntity userEntity = userRepository.findById(userDetailsImpl.getId()).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));

		Long userId = userEntity.getId();

		if (!commentEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 댓글을 생성한 사용자가 일치하지 않습니다."});
		}
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		commentRepository.delete(commentEntity);
	}
	
	@Override
	public PageResponse<CommentResponse> findAllByPostId(Long postId, PaginationUtil paginationUtil) {
		Pageable pageable = paginationUtil.makePageable();

		postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		Page<CommentEntity> comments = commentRepository.findByPostId(postId, pageable);

		List<CommentEntity> commentEntities = comments.getContent();
		int pageSize = comments.getSize();
		int pageNo = comments.getNumber();
		long totalElements = comments.getTotalElements();
		int totalPages = comments.getTotalPages();
		boolean isLast = comments.isLast();
		boolean isFirst = comments.isFirst();
		
		List<CommentResponse> commentResponse = commentEntities.stream().map((commentEntity) -> CommentMapper.INSTANCE.toDto(commentEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(commentResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public CommentResponse reply(Long id, Long postId, CommentCreateRequest commentCreate, UserDetailsImpl userDetailsImpl) {
		Long userId = userDetailsImpl.getId();

		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"사용자가 존재하지 않습니다."}));
		CommentEntity parentCommentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!parentCommentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		CommentEntity commentEntity = CommentMapper.INSTANCE.toEntity(commentCreate);
		
		commentEntity.parentId(parentCommentEntity.getId()).post(postEntity).user(userEntity);
				
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}
	
	@Override
	public CommentResponse update(Long id, Long postId, CommentUpdateRequest commentUpdate, UserDetailsImpl userDetailsImpl) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity commentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		UserEntity userEntity = userRepository.findById(userDetailsImpl.getId()).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));

		Long userId = userEntity.getId();
		
		if (!commentEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 댓글을 생성한 사용자가 일치하지 않습니다."});
		}
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		String content = commentUpdate.getContent();
			
		if (NotNullNotEmptyChecker.check(content)) {
			commentEntity.content(content);
		}
		
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}
}