package com.whooa.blog.comment.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDto {
	
	public static class CommentCreateRequest {
		@NotBlank(message = "이름을 입력하세요.")
		private String name;
		
		@NotBlank(message = "내용을 입력하세요.")
		private String content;
		
		@NotBlank(message = "비밀번호를 입력하세오.")
		private String password;
		
		public CommentCreateRequest(String name, String content, String password) {
			this.name = name;
			this.content = content;
			this.password = password;
		}
		
		public CommentCreateRequest() {}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "CommentCreateRequest [name=" + name + ", content=" + content + ", password=" + password + "]";
		}
	}
	
	public static class CommentUpdateRequest {
		private String content;
		
		@NotBlank(message = "비밀번호를 입력하세오.")
		private String password;
		
		public CommentUpdateRequest(String content, String password) {
			this.content = content;
			this.password = password;
		}
		
		public CommentUpdateRequest() {}
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "CommentUpdateRequest [content=" + content + ", password=" + password + "]";
		}
	}

	public static class CommentResponse {
		private Long id;
		private String name;
		private String content;
		private Long parentId;
			
		public CommentResponse(Long id, String name, String content, Long parentId) {
			this.id = id;
			this.name = name;
			this.content = content;
			this.parentId = parentId;
		}
		
		public CommentResponse(Long id, String name, String content) {
			this(id, name, content, -1L);
		}
		
		public CommentResponse() {}
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		/* 
		 * 순환 참조 오류(즉, 무한 재귀) 해결책.
		 * 1. PostResponse를 호출 -> PostResponse는 List<CommentDto> 소유 -> 모든 CommentDto는 다시 PostDto를 호출, 따라서 게터 제거하기.
		 * 2. 1번 객체는 @JsonManagedReferece 어노테이션, 2번 객체는 @JsonBackReference
		 * 	 문제는 2번 참조되는 객체를 얻을 수 없다.
		 * 3. 한 쌍의 DTO를 생성하고 엔티티에서 수동으로 채우기.
		 * public PostResponse getPost() {
		 *	 return post;
		 * }
		 */
		public Long getParentId() {
			return parentId;
		}

		public void setParentId(Long parentId) {
			this.parentId = parentId;
		}

		@Override
		public String toString() {
			return "CommentResponse [id=" + id + ", name=" + name + ", content=" + content + ", parentId=" + parentId + "]";
		}
	}
}