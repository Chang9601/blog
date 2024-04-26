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
	@Column(length = 300, nullable = false)
	private String name;
	
	@Column(length = 500, nullable = false)
	private String content;
	
	@Column(length = 500, nullable = false)
	private String password;
	
	/* 
	 * 지연 로딩(FetchType.LAZY)은 연관된 데이터를 실제 사용 시 조회한다. 
	 * 즉시 로딩(FetchType.EAGER)는 엔티티 조회 시 연관된 엔티티도 함께 조회한다.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	/*
	 * No property postId found for CommentEntity 오류 발생.
	 * 필드 이름을 postEntity로 설정했기 때문에 post_entity_id로 수정해야 하는데 데이터베이스의 외래키 이름이 복잡하다.
	 * 따라서, 필드 이름을 post로 수정한다.
	 */
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	/* 대댓글은 댓글이며 부모 댓글의 자식이기 때문에 엔티티로 만들지 않는다. */
	@Column(name = "parent_id", nullable = true)
	private Long parentId;
	
	public CommentEntity(Long id, String name, String content, Long parentId, String password, PostEntity post) {
		super(id);
		
		this.name = name;
		this.content = content;
		this.parentId = parentId;
		this.password = password;
		this.post = post;
	}

	// TODO: 생성자 정리.
	public CommentEntity(String name, String content, Long parentId, String password, PostEntity post) {
		this(-1L, name, content, parentId, password, post);
	}
	
	public CommentEntity(Long id, String name, String content, Long parentId) {
		this(id, name, content, parentId, null, null);
	}
	
	public CommentEntity(Long id, String name, String content, String password) {
		this(id, name, content, -1L, password, null);
	}
	
	public CommentEntity(String name, String content, Long parentId) {
		this(-1L, name, content, parentId, null, null);
	}
		
	public CommentEntity(String name, String content, String password) {
		this(-1L, name, content, -1L, password, null);
	}
	
	public CommentEntity() {
		super(-1L);
	}

	public Long getId() {
		return super.getId();
	}
	
	public void setId(Long id) {
		super.setId(id);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public PostEntity getPost() {
		return post;
	}

	public void setPost(PostEntity post) {
		if (this.post != null) {
			this.post.getComments().remove(this);
		}
		
		this.post = post;
		post.getComments().add(this);
	}
	
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}	

	@Override
	public String toString() {
		return "CommentEntity [id=" + super.getId() + ", name=" + name + ", content=" + content + ", password=" + password + ", post=" + post + ", parentId=" + parentId + "]";
	}
}