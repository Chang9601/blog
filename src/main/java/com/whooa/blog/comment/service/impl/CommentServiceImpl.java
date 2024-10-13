package com.whooa.blog.comment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentSearchRequest;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.comment.mapper.CommentMapper;
import com.whooa.blog.comment.repository.CommentQueryDslRepository;
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
import com.whooa.blog.util.StringUtil;
import com.whooa.blog.util.PaginationParam;

@Service
public class CommentServiceImpl implements CommentService {
	private CommentRepository commentRepository;
	private CommentQueryDslRepository commentQueryDslRepository;
	private PostRepository postRepository;
	private UserRepository userRepository;

	public CommentServiceImpl(CommentRepository commentRepository, CommentQueryDslRepository commentQueryDslRepository, PostRepository postRepository, UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.commentQueryDslRepository = commentQueryDslRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}
	
	@Override
	public CommentResponse create(Long postId, CommentCreateRequest commentCreate, UserDetailsImpl userDetailsImpl) {
		Long userId;
		CommentEntity commentEntity;
		PostEntity postEntity;
		UserEntity userEntity;
		
		userId = userDetailsImpl.getId();

		postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));	
		userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"사용자가 존재하지 않습니다."}));
		commentEntity = CommentMapper.INSTANCE.toEntity(commentCreate);
		
		commentEntity.setPost(postEntity);
		commentEntity.setUser(userEntity);
		
		return CommentMapper.INSTANCE.fromEntity(commentRepository.save(commentEntity));
	}
	
	@Override
	public void delete(Long id, Long postId, UserDetailsImpl userDetailsImpl) {
		Long userId;
		CommentEntity commentEntity;
		PostEntity postEntity;
		
		postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		commentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));

		userId = userDetailsImpl.getId();

		if (!commentEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 댓글을 생성한 사용자가 일치하지 않습니다."});
		}
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		commentRepository.delete(commentEntity);
	}
	
	@Override
	public PageResponse<CommentResponse> findAllByPostId(Long postId, PaginationParam paginationParam) {
		Pageable pageable;
		Page<CommentEntity> page;
		List<CommentEntity> commentEntities;
		List<CommentResponse> commentResponse;
		int pageSize, pageNo, totalPages;
		long totalElements;
		boolean isLast, isFirst;
		
		pageable = paginationParam.makePageable();

		postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		page = commentRepository.findByPostId(postId, pageable);

		commentEntities = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
		
		commentResponse = commentEntities.stream().map((commentEntity) -> CommentMapper.INSTANCE.fromEntity(commentEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(commentResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public CommentResponse reply(Long id, Long postId, CommentCreateRequest commentCreate, UserDetailsImpl userDetailsImpl) {
		Long userId;
		CommentEntity parentCommentEntity, commentEntity;
		PostEntity postEntity;
		UserEntity userEntity;
		
		userId = userDetailsImpl.getId();

		postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"사용자가 존재하지 않습니다."}));
		parentCommentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!parentCommentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		commentEntity = CommentMapper.INSTANCE.toEntity(commentCreate);
		
		commentEntity.setParentId(parentCommentEntity.getId());
		commentEntity.setPost(postEntity);
		commentEntity.setUser(userEntity);
		
		return CommentMapper.INSTANCE.fromEntity(commentRepository.save(commentEntity));
	}
	
	@Override
	public PageResponse<CommentResponse> search(CommentSearchRequest commentSearch, PaginationParam paginationParam) {
		Pageable pageable;
		Page<CommentEntity> page;
		List<CommentEntity> commentEntities;
		List<CommentResponse> commentResponse;
		int pageSize, pageNo, totalPages;
		long totalElements;
		boolean isLast, isFirst;
		
		pageable = paginationParam.makePageable();
		page = commentQueryDslRepository.search(commentSearch, pageable);
		
		commentEntities = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
		
		commentResponse = commentEntities.stream().map((commentEntity) -> CommentMapper.INSTANCE.fromEntity(commentEntity)).collect(Collectors.toList());

		return PageResponse.handleResponse(commentResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public CommentResponse update(Long id, Long postId, CommentUpdateRequest commentUpdate, UserDetailsImpl userDetailsImpl) {
		CommentEntity commentEntity;
		String content;
		PostEntity postEntity;
		Long userId;

		userId = userDetailsImpl.getId();

		postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		commentEntity = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!commentEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 댓글을 생성한 사용자가 일치하지 않습니다."});
		}
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		content = commentUpdate.getContent();
			
		if (StringUtil.notEmpty(content)) {
			commentEntity.setContent(content);
		}
		
		return CommentMapper.INSTANCE.fromEntity(commentRepository.save(commentEntity));
	}
}