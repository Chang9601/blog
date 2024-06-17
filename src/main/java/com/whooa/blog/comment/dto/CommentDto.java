package com.whooa.blog.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class CommentDto {

	@Schema(
		description = "댓글 생성 DTO"
	)
	public static class CommentCreateRequest {
		
		@Schema(
			description = "댓글 내용"
		)		
		@NotBlank(message = "내용을 입력하세요.")
		private String content;

		@Schema(
			description = "댓글 이름"
		)			
		@NotBlank(message = "이름을 입력하세요.")
		private String name;
		
		@Schema(
			description = "댓글 비밀번호"
		)		
		@NotBlank(message = "비밀번호를 입력하세오.")
		private String password;
		
		public CommentCreateRequest(String content, String name, String password) {
			this.content = content;
			this.name = name;
			this.password = password;
		}

		public CommentCreateRequest() {}
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "CommentCreateRequest [content=" + content + ", name=" + name + ", password=" + password + "]";
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
	
	public static class CommentDeleteRequest {		
		@NotBlank(message = "비밀번호를 입력하세오.")
		private String password;
		
		public CommentDeleteRequest(String password) {
			this.password = password;
		}
		
		public CommentDeleteRequest() {}
		
	
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "CommentDeleteRequest [password=" + password + "]";
		}
	}

	public static class CommentResponse {
		private Long id;
		private String content;
		private String name;
		private Long parentId;

		public CommentResponse(Long id, String content, String name, Long parentId) {
			this.id = id;
			this.content = content;
			this.name = name;
			this.parentId = parentId;
		}

		public CommentResponse() {}
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public Long getParentId() {
			return parentId;
		}

		public void setParentId(Long parentId) {
			this.parentId = parentId;
		}

		@Override
		public String toString() {
			return "CommentResponse [id=" + id + ", content=" + content + ", name=" + name + ", parentId=" + parentId
					+ "]";
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
	}
}