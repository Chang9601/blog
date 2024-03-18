package com.whooa.blog.post.dto;


public class PostDto {

	public static class Request {
		private String title;
		private String description;
		private String content;
		
		public Request(final String title, final String description, final String content) {
			this.title = title;
			this.description = description;
			this.content = content;
		}
		
		public String getTitle() {
			return title;
		}
		public String getDescription() {
			return description;
		}
		public String getContent() {
			return content;
		}
	}
	
	public static class Response {
		private String title;
		private String description;
		private String content;
		
		public String getTitle() {
			return title;
		}
		public String getDescription() {
			return description;
		}
		public String getContent() {
			return content;
		}		
	}
}