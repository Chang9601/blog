package com.whooa.blog.post.doc;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelation;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelations;
import org.springframework.data.elasticsearch.core.join.JoinField;

import com.fasterxml.jackson.annotation.JsonFormat;

@Document(indexName = "posts")
public class PostDoc {
	@Id
	@Field(type = FieldType.Keyword)
	private String id;
	
	@Field(type = FieldType.Text, name = "category_name")
	private String categoryName;
	
	@Field(type = FieldType.Text)
	private String content;
	
	@Field(type = FieldType.Text)
	private String title;
	
	@Field(type = FieldType.Keyword, name = "category_id")
	private Long categoryId;
	
	@Field(type = FieldType.Keyword, name = "comment_ids")
	private List<Long> commentIds;

	@Field(type = FieldType.Keyword, name = "user_id")
	private Long userId;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date createdAt;
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
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
		private String id;
		private String categoryName;
		private String content;
		private String title;
		private Long categoryId;
		private List<Long> commentIds;
		private Long userId;
		
		public PostDocBuilder() {}
		
		public PostDocBuilder id(String id) {
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
			PostDoc postDoc = new PostDoc();
			
			postDoc.setId(id);
			postDoc.setCategoryName(categoryName);
			postDoc.setContent(content);
			postDoc.setTitle(title);
			postDoc.setCategoryId(categoryId);
			postDoc.setUserId(userId);
			
			return postDoc;
		}
	}
}