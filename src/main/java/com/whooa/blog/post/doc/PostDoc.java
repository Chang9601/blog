package com.whooa.blog.post.doc;


import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.whooa.blog.common.doc.CoreDoc;

@Document(indexName = "posts")
public class PostDoc extends CoreDoc {	
	@Field(type = FieldType.Text, name = "category_name")
	private String categoryName;
	
	@Field(type = FieldType.Text)
	private String content;
	
	@Field(type = FieldType.Text)
	private String title;
	
	// TODO: 날짜 오류
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Field(type = FieldType.Date, name = "created_at")
	private Date createdAt; 
	
	public PostDoc() {}

	public Long getId() {
		return super.getId();
	}

	public void setId(Long id) {
		super.setId(id);
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
	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "PostDoc [categoryName=" + categoryName + ", content=" + content + ", title=" + title + ", createdAt="
				+ createdAt + "]";
	}
}