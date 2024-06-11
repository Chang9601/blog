package com.whooa.blog.category.dto;

public class CategoryDto {

	public static class CategoryCreateRequest {
		private String name;

		public CategoryCreateRequest(String name) {
			this.name = name;
		}
		
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
		private String name;

		public CategoryUpdateRequest(String name) {
			this.name = name;
		}
		
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

	public static class CategoryResponse {
		private Long id;
		private String name;
		
		public CategoryResponse(Long id, String name) {
			this.id = id;
			this.name = name;
		}
		
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