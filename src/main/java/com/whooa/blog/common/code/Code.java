package com.whooa.blog.common.code;

public enum Code {
	OK(200, "성공했습니다."),
	CREATED(201, "생성되었습니다."), 
	NO_CONTENT(204, "내용이 없습니다."),
	
	BAD_REQUEST(400, "잘못된 요청입니다."), 
	UNAUTHORIZED(401, "인증되어 있지 않습니다."),
	FORBIDDEN(403, "권한이 없습니다."),
	NOT_FOUND(404, "찾을 수 없습니다."), 
	CONFLICT(409, "충돌이 발생했습니다."),
	
	INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
	
	COMMENT_NOT_IN_POST(1000, "포스트에 해당 댓글이 없습니다."),
	
	DIRECTY_NOT_CREATED(2000, "디렉터리를 생성할 수 없습니다."),
	INVALID_PATH_SEQUENCE(2001, "유효하지 않은 경로 시퀀스가 포함되어 있습니다."), 
	FILE_NOT_SAVED(2002, "파일을 저장할 수 없습니다."),
	FILE_NOT_DOWNLOADED(2003, "파일을 다운로드할 수 없습니다."),
	
	INVALID_JWT(3000, "JWT 토큰이 유효하지 않습니다."),
	
	USER_NOT_MATCHED(4000, "사용자가 일치하지 않습니다.");

	private int code;
	private String message;
	
	private Code(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}