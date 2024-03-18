package com.whooa.blog.common.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.whooa.blog.common.code.Code;

public class ApiResponse<T> {
	private final Metadata metadata;
	private final T data;
	
	private ApiResponse(final int code, final String message, final T data, final String[] detail) {
		this.metadata = new Metadata(code, message, detail);
		this.data = data;
	}

	public static <T> ApiResponse<T> handleSuccess(final int code, final String message, final T data, final String[] detail) {
		final int successCode = code != -1 ? code : Code.OK.getCode();
		final String successMessage = message != null ? message : Code.OK.getMessage();
		
		return new ApiResponse<T>(successCode, successMessage, data, detail);
	}
	
	public static <T> ApiResponse<T> handleFailure(final int code, final String message, final T data, final String[] detail) {
		final int failureCode = code != -1 ? code : Code.INTERNAL_SERVER_ERROR.getCode();
		final String failureMessage = message != null ? message : Code.INTERNAL_SERVER_ERROR.getMessage();
		
		return new ApiResponse<T>(failureCode, failureMessage, data, detail);		
	}
	
	// 정적 클래스 사용 이유
	private static class Metadata {
		private final int code;
		private final String messsage;
		private final long timestamp;
		private final String[] detail;
		
		private Metadata(final int code, final String message, final String[] detail) {
			this.code = code;
			this.messsage = message;
			this.detail = detail;
			this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		}
	}
}
