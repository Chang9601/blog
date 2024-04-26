package com.whooa.blog.common.exception;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import com.whooa.blog.comment.exception.CommentNotBelongingToPostException;
import com.whooa.blog.comment.exception.CommentNotFoundException;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.user.exception.DuplicateUserException;
import com.whooa.blog.user.exception.UnauthenticatedUserException;
import com.whooa.blog.user.exception.UserNotFoundException;

/*
 * @RestControllerAdvice는 전역으로 예외를 처리하는 어노테이션.
 * 내부적으로 @Component 어노테이션을 사용해서 Spring 빈으로 등록된다.
 */
@RestControllerAdvice
public class AllExceptionHandler {
	private static  Logger logger = LoggerFactory.getLogger(AllExceptionHandler.class);

	@ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<PostNotFoundException> handleException(PostNotFoundException exception) {
		logger.error("PostNotFoundException: {}", exception.getException().getMessage());
		return ApiResponse.handleFailure(exception.getException().getCode(), exception.getException().getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<CommentNotFoundException> handleException(CommentNotFoundException exception) {
		logger.error("CommentNotFoundException: {}", exception.getException().getMessage());
		return ApiResponse.handleFailure(exception.getException().getCode(), exception.getException().getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(CommentNotBelongingToPostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<CommentNotBelongingToPostException> handleException(CommentNotBelongingToPostException exception) {
		logger.error("CommentNotBelongingToPostException: {}", exception.getException().getMessage());
		return ApiResponse.handleFailure(exception.getException().getCode(), exception.getException().getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
	public ApiResponse<DuplicateUserException> handleException(DuplicateUserException exception) {		
		logger.error("DuplicateUserException: {}", exception.getException().getMessage());
		return ApiResponse.handleFailure(exception.getException().getCode(), exception.getException().getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<UserNotFoundException> handleException(UserNotFoundException exception) {
		logger.error("UserNotFoundException: {}", exception.getException().getMessage());
		return ApiResponse.handleFailure(exception.getException().getCode(), exception.getException().getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(UnauthenticatedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<UnauthenticatedUserException> handleException(UnauthenticatedUserException exception) {
		logger.error("UnauthenticatedUserException: {}", exception.getException().getMessage());
		return ApiResponse.handleFailure(exception.getException().getCode(), exception.getException().getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<AccessDeniedException> handleException(AccessDeniedException exception) {
		logger.error("AccessDeniedException: {}", exception.getMessage());
		return ApiResponse.handleFailure(Code.UNAUTHORIZED.getCode(), Code.UNAUTHORIZED.getMessage(), null, new String[] {"인증되어 있지 않습니다."});
	}
	
	/* DTO 검증 메서드. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<MethodArgumentNotValidException> handleException(MethodArgumentNotValidException exception) {		
		List<String> errors = new ArrayList<>();
		
		exception.getBindingResult().getAllErrors().forEach((error) -> {
			String field = ((FieldError)error).getField();
			String message = error.getDefaultMessage() ;
			String detail = field + ": " + message;
			
			errors.add(detail);
		});
		
		logger.error("MethodArgumentNotValidException: {}", exception.getMessage());
		return ApiResponse.handleFailure(Code.BAD_REQUEST.getCode(), Code.BAD_REQUEST.getMessage(), null, errors.toArray(new String[0]));
	}
	
	/* 나머지 예외 처리 메서드. */
	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<Exception> handleException(Exception exception) {
		logger.error("Exception: {}", exception.getMessage());
		return ApiResponse.handleFailure(Code.INTERNAL_SERVER_ERROR.getCode(), Code.INTERNAL_SERVER_ERROR.getMessage(), null, new String[] {exception.getMessage()});
	}
}