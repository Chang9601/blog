package com.whooa.blog.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class CommentDto {

	public static class CommentCreateRequest {
		@NotBlank(message = "내용을 입력하세요.")
		@Schema(description = "댓글 생성 시 필요한 내용", example = "1번 댓글!", name = "내용")
		private String content;
		
		public CommentCreateRequest() {}
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "CommentCreateRequest [content=" + content + "]";
		}
	}
		
	public static class CommentUpdateRequest {
		@NotBlank(message = "내용을 입력하세요.")
		@Schema(description = "댓글 수정 시 필요한 내용", example = "1번 댓글!", name = "내용")
		private String content;

		public CommentUpdateRequest() {}
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "CommentUpdateRequest [content=" + content + "]";
		}
	}
	
	public static class CommentSearchRequest {
		@NotBlank(message = "내용을 입력하세요.")
		@Schema(description = "댓글 검색 시 필요한 내용", example = "1번 댓글!", name = "내용")
		private String content;

		public CommentSearchRequest() {}
		
		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "CommentUpdateRequest [content=" + content + "]";
		}
	}
	
	/* 
	 * 순환 참조 오류(즉, 무한 재귀) 해결책.
	 * 1. PostResponse를 호출 -> PostResponse는 List<CommentDto> 소유 -> 모든 CommentDto는 다시 PostDto를 호출, 따라서 게터 제거하기.
	 * 2. 1번 객체는 @JsonManagedReferece 어노테이션, 2번 객체는 @JsonBackReference
	 * 	  문제는 2번 참조되는 객체를 얻을 수 없다.
	 * 3. 한 쌍의 DTO를 생성하고 엔티티에서 수동으로 채우기.
	 *    public PostResponse getPost() {
	 *	      return post;
	 *    }
	 */
	public static class CommentResponse {
		@Schema(description = "데이터베이스에 저장된 댓글 아이디", example = "1", name = "아이디")
		private Long id;
		
		@Schema(description = "데이터베이스에 저장된 댓글 아이디", example = "1번 댓글!", name = "내용")
		private String content;
		
		@Schema(description = "데이터베이스에 저장된 대댓글 아이디", example = "3", name = "대댓글 아이디")
		private Long parentId;

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
		
		public Long getParentId() {
			return parentId;
		}

		public void setParentId(Long parentId) {
			this.parentId = parentId;
		}

		@Override
		public String toString() {
			return "CommentResponse [id=" + id + ", content=" + content + ", parentId=" + parentId + "]";
		}
	}
}