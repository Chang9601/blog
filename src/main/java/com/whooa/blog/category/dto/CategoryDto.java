package com.whooa.blog.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDto {

	@Schema(
		description = "카테고리 생성 DTO"
	)	
	public static class CategoryCreateRequest {
		@Schema(
			description = "카테고리 이름"
		)
		@Size(min = 2, message = "이름은 최소 2자 이상입니다.")
		@NotBlank(message = "이름을 입력하세요.")
		private String name;
		
		public CategoryCreateRequest() {}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "CategoryCreateRequest [name=" + name + "]";
		}
	}
	
	@Schema(
		description = "카테고리 수정 DTO"
	)	
	public static class CategoryUpdateRequest {
		@Schema(
			description = "카테고리 이름"
		)
		@Size(min = 2, message = "이름은 최소 2자 이상입니다.")
		@NotBlank(message = "이름을 입력하세요.")
		private String name;
		
		public CategoryUpdateRequest() {}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "CategoryUpdateRequest [name=" + name + "]";
		}
	}	

	@Schema(
		description = "카테고리 응답 DTO"
	)	
	public static class CategoryResponse {
		@Schema(
			description = "카테고리 아이디"
		)
		private Long id;
		
		@Schema(
			description = "카테고리 이름"
		)
		private String name;
		
		public CategoryResponse() {}
		
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

		@Override
		public String toString() {
			return "CategoryResponse [id=" + id + ", name=" + name + "]";
		}
	}
}