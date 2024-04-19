package com.whooa.blog.common.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

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

/*
  전역으로 예외를 처리하는 어노테이션.
  내부적으로 @Component 어노테이션을 사용해서 Spring 빈으로 등록된다.
*/
@RestControllerAdvice
public class AllExceptionHandler {

	@ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<PostNotFoundException> handleException(final PostNotFoundException exception) {
		return ApiResponse.handleFailure(exception.getCode(),exception.getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<CommentNotFoundException> handleException(final CommentNotFoundException exception) {
		return ApiResponse.handleFailure(exception.getCode(),exception.getMessage(), null, exception.getDetails());
	}
	
	@ExceptionHandler(CommentNotBelongingToPostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<CommentNotBelongingToPostException> handleException(final CommentNotBelongingToPostException exception) {
		return ApiResponse.handleFailure(exception.getCode(),exception.getMessage(), null, exception.getDetails());
	}
	
	// DTO 검증 메서드.
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<MethodArgumentNotValidException> handleException(final MethodArgumentNotValidException exception) {		
		List<String> errors = new ArrayList<>();
		
		exception.getBindingResult().getAllErrors().forEach((error) -> {
			String field = ((FieldError)error).getField();
			String message = error.getDefaultMessage() ;
			String detail = field + ": " + message;
			errors.add(detail);
		});
		
		return ApiResponse.handleFailure(Code.BAD_REQUEST.getCode(), Code.BAD_REQUEST.getMessage(), null, errors.toArray(new String[0]));
	}
	
	// 나머지 예외 처리 메서드.
	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<Exception> handleException(final Exception exception) {
		return ApiResponse.handleFailure(Code.INTERNAL_SERVER_ERROR.getCode(), Code.INTERNAL_SERVER_ERROR.getMessage(), null, new String[] {exception.getMessage()});
	}
}