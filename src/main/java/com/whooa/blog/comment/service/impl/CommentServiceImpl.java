package com.whooa.blog.comment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentDeleteRequest;
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
import com.whooa.blog.user.exception.InvalidCredentialsException;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.NotNullNotEmptyChecker;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.PasswordUtil;

@Service
public class CommentServiceImpl implements CommentService {
	private CommentRepository commentRepository;
	private PostRepository postRepository;
	private UserRepository userRepository;
	private PasswordUtil passwordUtil;


	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository, PasswordUtil passwordUtil) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.passwordUtil = passwordUtil;
	}
	
	@Override
	public CommentResponse create(UserDetailsImpl userDetailsImpl, Long postId, CommentCreateRequest commentCreate) {
		Long userId = userDetailsImpl.getId();

		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));	
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		CommentEntity commentEntity = CommentMapper.INSTANCE.toEntity(commentCreate);
		
		String plainPassword = commentEntity.getPassword();
		String hashedPassword = passwordUtil.hash(plainPassword);
		
		commentEntity.setPost(postEntity);
		commentEntity.setPassword(hashedPassword);
		commentEntity.setUser(userEntity);
		
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}

	@Override
	public PageResponse<CommentResponse> findAllByPostId(Long postId, PaginationUtil paginationUtil) {
		Pageable pageable = paginationUtil.makePageable();

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
	public CommentResponse update(Long postId, Long commentId, CommentUpdateRequest commentUpdate) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		String plainPassword = commentUpdate.getPassword();
		String hashedPassword = commentEntity.getPassword();
		String content = commentUpdate.getContent();
		
		if (!passwordUtil.match(plainPassword, hashedPassword)) {
			throw new InvalidCredentialsException(Code.BAD_REQUEST, new String[] {"비밀번호가 일치하지 않습니다."});
		}
		
		if (NotNullNotEmptyChecker.check(content)) {
			commentEntity.setContent(content);
		}
		
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}

	@Override
	public void delete(Long postId, Long commentId, CommentDeleteRequest commentDelete) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!commentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		String plainPassword = commentDelete.getPassword();
		String hashedPassword = commentEntity.getPassword();
		
		if (!passwordUtil.match(plainPassword, hashedPassword)) {
			throw new InvalidCredentialsException(Code.BAD_REQUEST, new String[] {"비밀번호가 일치하지 않습니다."});
		}
		
		commentRepository.delete(commentEntity);
	}

	@Override
	public CommentResponse reply(Long postId, Long commentId, CommentCreateRequest commentCreate) {
		PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		CommentEntity parentCommentEntity = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(Code.NOT_FOUND, new String[] {"댓글이 존재하지 않습니다."}));
		
		if (!parentCommentEntity.getPost().getId().equals(postEntity.getId())) {
			throw new CommentNotBelongingToPostException(Code.COMMENT_NOT_IN_POST, new String[] {"댓글이 포스트에 속하지 않습니다."});
		}
		
		CommentEntity commentEntity = CommentMapper.INSTANCE.toEntity(commentCreate);
		commentEntity.setParentId(parentCommentEntity.getId());
		commentEntity.setPost(postEntity);
				
		return CommentMapper.INSTANCE.toDto(commentRepository.save(commentEntity));
	}
}