package com.whooa.blog.post.dto;

import jakarta.validation.constraints.NotBlank;

/*
  DTO 사용 이유.
  1. 필요없거나 민감한 데이터를 제외하고 필요한 데이터만을 응답으로 전달할 수 있다.
  2. 엔티티 구현을 캡슐화하여 보호할 수 있다. 
  	 만약 DTO가 없다면 클라이언트의 요청과 엔티티 모델이 강하게 결합되어 클라이언트 변화가 엔티티에 영향을 끼친다.
  	 엔티티는 도메인의 핵심 논리와 속성을 갖고 있으며 DB 테이블에 대응되는 클래스이므로 함부로 변경되지 않아야 한다.
  3. 검증 코드와 엔티티의 속성 코드를 분리할 수 있다.
  엔티티 클래스에 @Column, @OneToOne와 같은 어노테이션들이 사용되는데 @Min, @Lenth와 같은 검증 코드가 사용되면 엔티티 클래스가 복잡하다.
*/
public class PostDto {

	public static class CreateRequest {
		@NotBlank(message = "제목을 입력하세요.")
		private String title;

		@NotBlank(message = "내용을 입력하세요.")
		private String content;
		
		public CreateRequest(final String title, final String content) {
			this.title = title;
			this.content = content;
		}
		
		public CreateRequest() {}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(final String title) {
			this.title = title;
		}
		
		public String getContent() {
			return content;
		}
		
		public void setContent(final String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "Request [title=" + title + ", content=" + content + "]";
		}
	}
	
	public static class UpdateRequest {
		private String title;
		private String content;
		
		public UpdateRequest(final String title, final String content) {
			this.title = title;
			this.content = content;
		}
		
		public UpdateRequest() {}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(final String title) {
			this.title = title;
		}
		
		
		public String getContent() {
			return content;
		}
		
		public void setContent(final String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "Request [title=" + title + ", content=" + content + "]";
		}
	}
	
	public static class Response {
		private Long id;
		private String title;
		private String content;
		
		public Response(final Long id, final String title, final String content) {
			this.id = id;
			this.title = title;
			this.content = content;
		}
		
		public Response() {}

		public Long getId() {
			return id;
		}
		
		public void setId(final Long id) {
			this.id = id;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(final String title) {
			this.title = title;
		}
		
		public String getContent() {
			return content;
		}
		
		public void setContent(final String content) {
			this.content = content;
		}
		
		@Override
		public String toString() {
			return "Response [id=" + id + ", title=" + title + ", content=" + content
					+ "]";
		}
	}
}