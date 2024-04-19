package com.whooa.blog.common.code;

public enum Code {
	OK(200, "성공했습니다."), CREATED(201, "생성되었습니다."), NO_CONTENT(204, "내용이 없습니다."),
	
	BAD_REQUEST(400, "잘못된 요청입니다."), NOT_FOUND(404, "찾을 수 없습니다."), CONFLICT(409, "충돌이 발생했습니다."),
	
	INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
	
	COMMENT_NOT_IN_POST(1000, "포스트에 해당 댓글이 없습니다.");
	
	private final int code;
	private final String message;
	
	private Code(final int code, final String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
}