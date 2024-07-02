package com.whooa.blog.comment.entity;

import com.whooa.blog.common.entity.CoreEntity;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comment")
public class CommentEntity extends CoreEntity {
	@Column(length = 500, nullable = false)
	private String content;
	
	@Column(length = 300, nullable = false)
	private String name;

	/* 대댓글은 댓글이며 부모 댓글의 자식이기 때문에 엔티티로 만들지 않는다. */
	@Column(name = "parent_id", nullable = true)
	private Long parentId;
	
	@Column(length = 500, nullable = false)
	private String password;
	
	/* 
	 * 지연 로딩(FetchType.LAZY)은 연관된 데이터를 실제 사용 시 조회한다. 
	 * 즉시 로딩(FetchType.EAGER)는 엔티티 조회 시 연관된 엔티티도 함께 조회한다.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	
	public CommentEntity(Long id, String content, String name, Long parentId, String password, PostEntity post, UserEntity user) {
		super(id);
		
		this.content = content;
		this.name = name;
		this.parentId = parentId;
		this.password = password;
		this.post = post;
		this.user = user;
	}

	public CommentEntity() {
		super(-1L);
	}
	
	public CommentEntity content(String content) {
		this.content = content;
		return this;
	}
	
	public CommentEntity name(String name) {
		this.name = name;
		return this;
	}
	
	public CommentEntity parentId(Long parentId) {
		this.parentId = parentId;
		return this;
	}
	
	public CommentEntity password(String password) {
		this.password = password;
		return this;
	}
	
	public CommentEntity post(PostEntity post) {
		if (this.post != null) {
			this.post.getComments().remove(this);
		}
		
		this.post = post;
		post.getComments().add(this);
		
		return this;
	}
	
	public CommentEntity user(UserEntity user) {
		if (this.user != null) {
			this.user.getComments().remove(this);
		}

		this.user = user;
		user.getComments().add(this);
		
		return this;
	}

	public Long getId() {
		return super.getId();
	}
	
	public void setId(Long id) {
		super.setId(id);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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
	
	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		if (this.user != null) {
			this.user.getComments().remove(this);
		}

		this.user = user;
		user.getComments().add(this);
	}

	@Override
	public String toString() {
		return "CommentEntity [id=" + super.getId() + ", content=" + content + ", name=" + name + ", parentId=" + parentId + ", password="
				+ password + ", post=" + post + ", user=" + user + "]";
	}
}