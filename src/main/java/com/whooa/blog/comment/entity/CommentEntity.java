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
@Table(name = "comments")
public class CommentEntity extends CoreEntity {
	@Column(length = 500, nullable = false)
	private String content;

	/* 대댓글은 댓글이며 부모 댓글의 자식이기 때문에 엔티티로 만들지 않는다. */
	@Column(name = "parent_id", nullable = true)
	private Long parentId;
	
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
	
	public CommentEntity() {
		super(0L);
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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
		return "CommentEntity [id=" + super.getId() + ", content=" + content + ", parentId=" + parentId + ", post=" + post + ", user=" + user + "]";
	}
	
	public static CommentEntityBuilder builder() {
		return new CommentEntityBuilder();
	}
	
	public static final class CommentEntityBuilder {
		private Long id;
		private String content;
		private Long parentId;
		private PostEntity post;
		private UserEntity user;
		
		private CommentEntityBuilder() {}
		
		public CommentEntityBuilder id(Long id) {
			this.id = id;
			return this;
		}
		
		public CommentEntityBuilder content(String content) {
			this.content = content;
			return this;
		}
		
		public CommentEntityBuilder parentId(Long parentId) {
			this.parentId = parentId;
			return this;
		}
		
		public CommentEntityBuilder post(PostEntity post) {
			this.post = post;
			return this;
		}
		
		public CommentEntityBuilder user(UserEntity user) {
			this.user = user;
			return this;
		}
		
		
		public CommentEntity build() {
			CommentEntity commentEntity = new CommentEntity();
			
			if (id != null) {
				commentEntity.setId(id);
			}
			
			commentEntity.setContent(content);
			
			if (parentId != null) {
				commentEntity.setParentId(parentId);
			}
			
			commentEntity.setPost(post);
			commentEntity.setUser(user);
			
			return commentEntity;
		}
	}
}