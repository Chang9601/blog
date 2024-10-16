package com.whooa.blog.category.dto;


import org.hibernate.validator.constraints.Length;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class CategoryDto {

	public static class CategoryCreateRequest {
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "카테고리 수정 시 필요한 이름", example = "운영체제", minLength = 2, name = "name")
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
		
	public static class CategoryUpdateRequest {
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "카테고리 생성 시 필요한 이름", example = "운영체제", minLength = 2, name = "name")
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

	public static class CategorySearchRequest {
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "카테고리 검색 시 필요한 이름", example = "운영체제", minLength = 2, name = "name")
		private String name;
		
		public CategorySearchRequest() {}
		
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
	
	public static class CategoryResponse {
		@Schema(description = "데이터베이스에 저장된 카테고리 아이디", example = "1", name = "id")
		private Long id;
		
		@Schema(description = "데이터베이스에 저장된 카테고리 이름", example = "운영체제", name = "name")
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