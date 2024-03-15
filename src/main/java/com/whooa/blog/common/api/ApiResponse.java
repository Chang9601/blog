package com.whooa.blog.common.api;

import com.whooa.blog.common.code.Code;

public class ApiResponse<T> {
	private final Metadata metadata;
	private final T data;
	
	private ApiResponse(int code, String message, T data?, String[] detail?) {
		this.metadata = new Metadata(code, message, detail);
		this.data = data;
	}

	public static <T> ApiResponse<T> handleSuccess(int code?, String message?, T data?, String[] detail?) {
		final int successCode = code || Code.OK.getCode();
		final String successMessage = message || Code.OK.getMessage();
		
		return new ApiResponse(successCode, successMEssage, data, detail);
	}
	
	public static <T> ApiResponse<T> handleFailure(int code?, String message?, T data?, String[] detail?) {
		final int failureCode = code || Code.INTERNAL_SERVER_ERROR.getCode();
		final String failureMessage = message || Code.INTERNAL_SERVER_ERROR.getMessage();
		
		return new ApiResponse(successCode, successMEssage, data, detail);		
	}
	
	public static class Metadata {
		private final int code;
		private final String messsage;
		private final long timestamp;
		// 선택 사항.
		private final String[] detail;
		// 선택 사항.
		
		public Metadata(int code, String message, String[] detail?) {
			this.code = code;
			this.messsage = message;
			this.detail = detail;
		}
	}
}
