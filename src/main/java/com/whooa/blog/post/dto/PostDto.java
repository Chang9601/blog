package com.whooa.blog.post.dto;

import java.util.List;

import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.file.dto.FileDto;

import jakarta.validation.constraints.NotBlank;

/*
 * DTO 사용 이유.
 * 1. 필요없거나 민감한 데이터를 제외하고 필요한 데이터만을 응답으로 전달할 수 있다.
 * 2. 엔티티 구현을 캡슐화하여 보호할 수 있다. 
 * 	 만약 DTO가 없다면 클라이언트의 요청과 엔티티 모델이 강하게 결합되어 클라이언트 변화가 엔티티에 영향을 끼친다.
 * 	 엔티티는 도메인의 핵심 논리와 속성을 갖고 있으며 데이터베이스 테이블에 대응되는 클래스이므로 함부로 변경되지 않아야 한다.
 * 3. 검증 코드와 엔티티의 속성 코드를 분리할 수 있다.
 * 	 엔티티 클래스에 @Column, @OneToOne와 같은 어노테이션들이 사용되는데 @Min, @Lenth와 같은 검증 코드가 사용되면 엔티티 클래스가 복잡하다.
 */
public class PostDto {

	public static class PostCreateRequest {
		@NotBlank(message = "제목을 입력하세요.")
		private String title;

		@NotBlank(message = "내용을 입력하세요.")
		private String content;
	
		public PostCreateRequest(String title, String content) {
			this.title = title;
			this.content = content;
		}
		
		public PostCreateRequest() {}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getContent() {
			return content;
		}
		
		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "Request [title=" + title + ", content=" + content + "]";
		}
	}
	
	public static class PostUpdateRequest {
		private String title;
		private String content;
		
		public PostUpdateRequest(String title, String content) {
			this.title = title;
			this.content = content;
		}
		
		public PostUpdateRequest() {}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		
		public String getContent() {
			return content;
		}
		
		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return "Request [title=" + title + ", content=" + content + "]";
		}
	}
	
	public static class PostResponse {
		private Long id;
		private String title;
		private String content;
		/*
		 DTO의 필드로 엔티티를 사용하게 되면 엔티티는 DTO에서 해야 할 일을 같이 해야 하기 때문에 변경에 대한 이유가 늘어난다.
		 다시말해, 이는 유지보수적인 측면에서 좋지 않기 때문에 DTO를 사용한다.
		*/
		private List<CommentResponse> comments;
		private List<FileDto> files;

		public PostResponse(Long id, String title, String content,  List<CommentResponse> comments, List<FileDto> files) {
			this.id = id;
			this.title = title;
			this.content = content;
			this.comments = comments;
			this.files = files;
		}

		public PostResponse(Long id, String title, String content) {
			this(id, title, content, null, null);
		}
		
		public PostResponse() {}

		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getContent() {
			return content;
		}
		
		public void setContent(String content) {
			this.content = content;
		}
		
		public List<FileDto> getFiles() {
			return files;
		}

		public void setFiles(List<FileDto> files) {
			this.files = files;
		}
		
		public List<CommentResponse> getComments() {
			return comments;
		}

		public void setComments(List<CommentResponse> comments) {
			this.comments = comments;
		}

		@Override
		public String toString() {
			return "PostResponse [id=" + id + ", title=" + title + ", content=" + content + ", files=" + files + ", comments= +]";
		}
	}
}