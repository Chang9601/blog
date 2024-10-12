package com.whooa.blog.post.doc;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelation;
import org.springframework.data.elasticsearch.annotations.JoinTypeRelations;
import org.springframework.data.elasticsearch.core.join.JoinField;

import com.whooa.blog.common.doc.CoreDoc;

@Document(indexName = "posts")
public class PostDoc extends CoreDoc {	
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
	
	@JoinTypeRelations(
		relations = {
			@JoinTypeRelation(parent = "category", children = "post"),
		}
	)
	private JoinField<String> relation;
	
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

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public List<Long> getCommentIds() {
		return commentIds;
	}

	public void setCommentIds(List<Long> commentIds) {
		this.commentIds = commentIds;
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
}