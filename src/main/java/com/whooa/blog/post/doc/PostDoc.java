package com.whooa.blog.post.doc;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelation;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelations;
import org.springframework.data.elasticsearch.core.join.JoinField;

@Document(indexName = "posts")
public class PostDoc {
	@Id
	@Field(type = FieldType.Keyword)
	private Long id;
	
	@Field(type = FieldType.Text, name = "category_name")
	private String categoryName;
	
	@Field(type = FieldType.Text)
	private String content;
	
	@Field(type = FieldType.Text)
	private String title;
	
	@Field(type = FieldType.Keyword, name = "category_id")
	private Long categoryId;

	@Field(type = FieldType.Keyword, name = "user_id")
	private Long userId;
	
	@JoinTypeRelations(
		relations = {
			@JoinTypeRelation(parent = "category", children = "post"),
		}
	)
	private JoinField<String> relation;
	
	public PostDoc() {}
	
	public static PostDocBuilder builder() {
		return new PostDocBuilder();
	}

	public Long getId() {
		return id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public JoinField<String> getRelation() {
		return relation;
	}

	public void setRelation(JoinField<String> relation) {
		this.relation = relation;
	}

	public static final class PostDocBuilder {
		private Long id;
		private String categoryName;
		private String content;
		private String title;
		private Long categoryId;
		private Long userId;
		
		public PostDocBuilder() {}
		
		public PostDocBuilder id(Long id) {
			this.id = id;
			return this;
		}
		
		public PostDocBuilder categoryName(String categoryName) {
			this.categoryName = categoryName;
			return this;
		}
		
		public PostDocBuilder content(String content) {
			this.content = content;
			return this;
		}
		
		public PostDocBuilder title(String title) {
			this.title = title;
			return this;
		}
		
		public PostDocBuilder categoryId(Long categoryId) {
			this.categoryId = categoryId;
			return this;
		}
		
		public PostDocBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public PostDoc build() {
			PostDoc postDocument = new PostDoc();
			
			postDocument.setId(id);
			postDocument.setCategoryName(categoryName);
			postDocument.setContent(content);
			postDocument.setTitle(title);
			postDocument.setCategoryId(categoryId);
			postDocument.setUserId(userId);
			
			return postDocument;
		}
	}
}