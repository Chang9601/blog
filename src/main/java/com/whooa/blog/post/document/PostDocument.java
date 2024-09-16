package com.whooa.blog.post.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelation;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelations;
import org.springframework.data.elasticsearch.core.join.JoinField;

@Document(indexName = "posts")
public class PostDocument {
	@Id
	@Field(type = FieldType.Keyword)
	private Long id;
	
	@Field(type = FieldType.Text)
	private String content;
	
	@Field(type = FieldType.Text)
	private String title;
	
	@Field(type = FieldType.Keyword)
	private Long categoryId;

	@Field(type = FieldType.Keyword)
	private Long userId;
	
	@JoinTypeRelations(
		relations = {
			@JoinTypeRelation(parent = "category", children = "post"),
			@JoinTypeRelation(parent = "user", children = "post")
		}
	)
	private JoinField<String> relation;
	
	public PostDocument() {}
	
	public static PostDocumentBuilder builder() {
		return new PostDocumentBuilder();
	}

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

	public static final class PostDocumentBuilder {
		private Long id;
		private String content;
		private String title;
		private Long categoryId;
		private Long userId;
		
		public PostDocumentBuilder() {}
		
		public PostDocumentBuilder id(Long id) {
			this.id = id;
			return this;
		}
		
		public PostDocumentBuilder content(String content) {
			this.content = content;
			return this;
		}
		
		public PostDocumentBuilder title(String title) {
			this.title = title;
			return this;
		}
		
		public PostDocumentBuilder categoryId(Long categoryId) {
			this.categoryId = categoryId;
			return this;
		}
		
		public PostDocumentBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public PostDocument build() {
			PostDocument postDocument = new PostDocument();
			
			postDocument.setId(id);
			postDocument.setContent(content);
			postDocument.setTitle(title);
			postDocument.setCategoryId(categoryId);
			postDocument.setUserId(userId);
			
			return postDocument;
		}
	}
}