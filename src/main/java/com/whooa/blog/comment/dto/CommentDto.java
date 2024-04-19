package com.whooa.blog.comment.dto;

import com.whooa.blog.post.entity.PostEntity;

import jakarta.validation.constraints.NotBlank;

public class CommentDto {
	
	public static class CreateRequest {
		@NotBlank(message = "이름을 입력하세요.")
		private String name;
		
		@NotBlank(message = "내용을 입력하세요.")
		private String content;
		
		@NotBlank(message = "비밀번호를 입력하세오.")
		private String password;
		
		public CreateRequest(final String name, final String content, final String password) {
			this.name = name;
			this.content = content;
			this.password = password;
		}
		
		public CreateRequest() {}
		
		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getContent() {
			return content;
		}

		public void setContent(final String content) {
			this.content = content;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(final String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "CreateRequest [name=" + name + ", content=" + content + ", password=" + password + "]";
		}
	}
	
	public static class UpdateRequest {
		private String content;
		
		@NotBlank(message = "비밀번호를 입력하세오.")
		private String password;
		
		public UpdateRequest(final String content, final String password) {
			this.content = content;
			this.password = password;
		}
		
		public UpdateRequest() {}
		
		public String getContent() {
			return content;
		}

		public void setContent(final String content) {
			this.content = content;
		}
		
		public String getPassword() {
			return password;
		}

		public void setPassword(final String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "UpdateRequest [content=" + content + ", password=" + password + "]";
		}
	}

	public static class Response {
		private Long id;
		private String name;
		private String content;
		private Long parentId;
		private PostEntity post;
		
		public Response(final Long id, final String name, final String content, final PostEntity post, final Long parentId) {
			this.id = id;
			this.name = name;
			this.content = content;
			this.parentId = parentId;
			this.post = post;
		}
		
		public Response(final Long id, final String name, final String content, final PostEntity post) {
			this(id, name, content, post, -1L);
		}
		
		public Response() {}
		
		public Long getId() {
			return id;
		}

		public void setId(final Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getContent() {
			return content;
		}

		public void setContent(final String content) {
			this.content = content;
		}
		/*
		  순환 참조 오류(즉, 무한 재귀) 해결책.
		  1. PostEntity를 호출 -> PostEntity는 Set<CommentEntity> 소유 -> 모든 CommentEntity는 다시 PostEntity를 호출 
		  2. 1번 객체는 @JsonManagedReferece 어노테이션, 2번 객체는 @JsonBackReference
		  	 문제는 2번 참조되는 객체를 얻을 수 없다.
		  3. 한 쌍의 DTO를 생성하고 엔티티에서 수동으로 채우기.
		  public PostEntity getPost() {
			  return post;
		  }
		*/
		public void setPostEntity(final PostEntity post) {
			this.post = post;
		}
		
		
		public Long getParentId() {
			return parentId;
		}

		public void setParentId(final Long parentId) {
			this.parentId = parentId;
		}

		@Override
		public String toString() {
			return "Response [id=" + id + ", name=" + name + ", content=" + content + ", post=" + post + "]";
		}
	}
}