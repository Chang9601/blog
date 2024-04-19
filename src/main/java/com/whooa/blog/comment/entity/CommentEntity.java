package com.whooa.blog.comment.entity;

import com.whooa.blog.common.entity.AbstractEntity;
import com.whooa.blog.post.entity.PostEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comment")
public class CommentEntity extends AbstractEntity {
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private String password;
	
	/* 대댓글은 댓글이며 부모 댓글의 자식이기 때문에 엔티티로 만들지 않는다. */
	@Column(name = "parent_id", nullable = true)
	private Long parentId;
	
	/* FetchType.LAZY는 Hibernate에게 관련된 엔티티를 관계를 사용할 때에만 데이터베이스에서 가져오도록 지시한다. */
	@ManyToOne(fetch = FetchType.LAZY)
	/*
	  No property postId found for CommentEntity 오류 발생.
	  필드 이름을 postEntity로 설정했기 때문에 post_entity_id로 수정해야 하는데 데이터베이스의 외래키 이름이 복잡하다.
	  따라서, 필드 이름을 post로 수정한다.
	*/
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;
	
	public CommentEntity(final Long id, final String name, final String content, final String password, final Long parentId) {
		super(id);
		
		this.name = name;
		this.content = content;
		this.password = password;
		this.parentId = parentId;
	}
	
	public CommentEntity(final Long id, final String name, final String content, final String password) {
		this(id, name, content, password, -1L);
	}
	
	public CommentEntity(final String name, final String content, final String password, final Long parentId) {
		this(-1L, name, content, password, parentId);
	}

	public CommentEntity(final String name, final String content, final String password) {
		this(-1L, name, content, password, -1L);
	}
	
	public CommentEntity() {
		this(-1L, null, null, null);
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}
	
	
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(final Long parentId) {
		this.parentId = parentId;
	}

	public PostEntity getPost() {
		return post;
	}

	public void setPost(final PostEntity post) {
		this.post = post;
	}

	@Override
	public String toString() {
		return "CommentEntity [id=" + super.getId() + ", name=" + name + ", content=" + content + ", password=" + password + "]";
	}
}