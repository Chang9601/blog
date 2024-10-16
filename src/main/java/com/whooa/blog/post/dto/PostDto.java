package com.whooa.blog.post.dto;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.file.value.File;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/*
 * DTO 사용 이유.
 * 1. 필요없거나 민감한 데이터를 제외하고 필요한 데이터만을 응답으로 전달할 수 있다.
 * 2. 엔티티 구현을 캡슐화하여 보호할 수 있다. 
 * 	  만약 DTO가 없다면 클라이언트의 요청과 엔티티 모델이 강하게 결합되어 클라이언트 변화가 엔티티에 영향을 끼친다.
 * 	  엔티티는 도메인의 핵심 논리와 속성을 갖고 있으며 데이터베이스 테이블에 대응되는 클래스이므로 함부로 변경되지 않아야 한다.
 * 3. 검증 코드와 엔티티의 속성 코드를 분리할 수 있다.
 * 	  엔티티 클래스에 @Column, @OneToOne와 같은 어노테이션들이 사용되는데 @Min, @Lenth와 같은 검증 코드가 사용되면 엔티티 클래스가 복잡하다.
 */
public class PostDto {

	public static class PostCreateRequest {
		@Length(message = "카테고리는 최소 2자 이상입니다.", min = 2)
		@NotBlank(message = "카테고리를 입력하세요.")
		@Schema(description = "포스트 생성 시 필요한 카테고리 이름", example = "운영체제", name = "categoryName")
		private String categoryName;
		
		@Length(message = "내용은 최소 1자 이상 최대 2000자 이하입니다.", max = 2000, min = 1)
		@NotBlank(message = "내용을 입력하세요.")
		@Schema(description = "포스트 생성 시 필요한 포스트 내용", example = "100자 이상의 포스트", name = "content")
		private String content;
		
		@Length(min = 2, message = "제목은 최소 2자 이상입니다.")
		@NotBlank(message = "제목을 입력하세요.")
		@Schema(description = "포스트 생성 시 필요한 포스트 제목", example = "스케줄링", name = "title")
		private String title;

		public PostCreateRequest() {}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return "PostCreateRequest [categoryName=" + categoryName + ", content=" + content + ", title=" + title
					+ "]";
		}
	}
	
	public static class PostUpdateRequest {
		@Length(message = "카테고리는 최소 2자 이상입니다.", min = 2)
		@NotBlank(message = "카테고리를 입력하세요.")
		@Schema(description = "포스트 수정 시 필요한 카테고리 이름", example = "운영체제", name = "categoryName")
		private String categoryName;
				
		@Length(message = "내용은 최소 1자 이상 최대 2000자 이하입니다.", max = 2000, min = 1)
		@NotBlank(message = "내용을 입력하세요.")
		@Schema(description = "포스트 수정 시 필요한 포스트 내용", example = "100자 이상의 포스트", name = "content")
		private String content;
		
		@Length(min = 2, message = "제목은 최소 2자 이상입니다.")
		@NotBlank(message = "제목을 입력하세요.")
		@Schema(description = "포스트 수정 시 필요한 포스트 제목", example = "스케줄링", name = "title")
		private String title;

		public PostUpdateRequest() {}
		
		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return "PostUpdateRequest [categoryName=" + categoryName + ", content=" + content + ", title=" + title
					+ "]";
		}
	}
	
	public static class PostResponse {
		@Schema(description = "데이터베이스에 저장된 포스트 아이디", example = "1", name = "id")
		private Long id;
		/*
		 * DTO의 필드로 엔티티를 사용하게 되면 엔티티는 DTO에서 해야 할 일을 같이 해야 하기 때문에 변경에 대한 이유가 늘어난다.
		 * 다시말해, 이는 유지보수적인 측면에서 좋지 않기 때문에 DTO를 사용한다.
		 */
		@Schema(description = "데이터베이스에 저장된 포스트 내용", example = "1번 포스트!", name = "content")
		private String content;
		
		@Schema(description = "데이터베이스에 저장된 포스트 제목", example = "스케줄링", name = "title")
		private String title;

		@Schema(contentSchema = CategoryResponse.class, description = "데이터베이스에 저장된 포스트의 카테고리", name = "category")
		private CategoryResponse category;
		
		@Schema(contentSchema = CommentResponse.class, description = "데이터베이스에 저장된 포스트의 댓글 목록", name = "comments")
		private List<CommentResponse> comments;
		
		@Schema(contentSchema = File.class, description = "데이터베이스에 저장된 포스트의 파일 목록", name = "files")
		private List<File> files;

		public PostResponse() {}
		
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
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
		public CategoryResponse getCategory() {
			return category;
		}

		public void setCategory(CategoryResponse category) {
			this.category = category;
		}

		public List<CommentResponse> getComments() {
			return comments;
		}

		public void setComments(List<CommentResponse> comments) {
			this.comments = comments;
		}

		public List<File> getFiles() {
			return files;
		}

		public void setFiles(List<File> files) {
			this.files = files;
		}

		@Override
		public String toString() {
			return "PostResponse [id=" + id + ", content=" + content + ", title=" + title + ", category=" + category
					+ ", comments=" + comments + ", files=" + files + "]";
		}
 	}
}