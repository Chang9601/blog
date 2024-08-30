package com.whooa.blog.comment.param;

import com.whooa.blog.util.PaginationParam;

public class CommentSearchParam extends PaginationParam {
	private String content;
	
	public CommentSearchParam() {}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "CommentSearchParam [content=" + content + "]";
	}
}