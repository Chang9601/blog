package com.whooa.blog.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.common.entity.CoreEntity;
import com.whooa.blog.file.value.File;
import com.whooa.blog.user.entity.UserEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "post")
public class PostEntity extends CoreEntity {
	@Column(length = 2000, nullable = false)
	private String content;
	
	@Column(length = 300, nullable = false)
	private String title;
	
	/*
	 * 데이터세트의 관계를 결정하는 관계형 측면에서 참조가 아니라 삽입을 사용한다. 즉, 일대다(구체적으로, 1:소.).
	 * 데이터의 읽기 및 쓰기 연산의 빈도(즉, 높은 읽기/쓰기 비율)를 결정하는 데이터 접근 패턴 측면에서 파일은 대부분의 경우 읽기 연산에 사용되며 쉽게 변하지 않는다.
	 * 데이터의 관련성 및 쿼리 측면에서 데이터세트는 관련성이 높다.
	 */
	@ElementCollection
	@CollectionTable(name = "file", joinColumns = @JoinColumn(name = "post_id"))
	private List<File> files = new ArrayList<File>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private CategoryEntity category;
	
	@OneToMany(mappedBy = "post")
	private List<CommentEntity> comments = new ArrayList<CommentEntity>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	/*
	 * List, Set과 같은 자료구조는 엔티티 생성 시 바로 초기화를 하기 때문에 생성자에 포함하지 않는다.
	 * 포함하면 게터에서 NullPointerException이 발생한다. 
	 */
	public PostEntity(Long id, String content, String title) {
		super(id);
		
		this.content = content;
		this.title = title;
	}

	public PostEntity() {
		super(-1L);
	}
	
	public PostEntity content(String content) {
		this.content = content;
		return this;
	}

	public PostEntity title(String title) {
		this.title = title;
		return this;
	}
	
	public PostEntity files(List<File> files) {
		this.files = files;
		return this;
	}

	public PostEntity category(CategoryEntity category) {
		if (this.category != null) {
			this.category.getPosts().remove(this);
		}
		
		this.category = category;
		category.getPosts().add(this);
		
		return this;
	}

	public PostEntity comments(List<CommentEntity> comments) {
		this.comments = comments;
		return this;
	}

	public PostEntity user(UserEntity user) {
		if (this.user != null) {
			this.user.getPosts().remove(this);
		}

		this.user = user;
		user.getPosts().add(this);
		
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
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	public CategoryEntity getCategory() {
		return category;
	}

	public void setCategory(CategoryEntity category) {
		if (this.category != null) {
			this.category.getPosts().remove(this);
		}
		
		this.category = category;
		category.getPosts().add(this);
	}
	
	public List<CommentEntity> getComments() {
		return comments;
	}

	public void setComments(List<CommentEntity> comments) {
		this.comments = comments;
	}
	
	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		if (this.user != null) {
			this.user.getPosts().remove(this);
		}

		this.user = user;
		user.getPosts().add(this);
	}

	@Override
	public String toString() {
		return "PostEntity [id=" + super.getId() + ", content=" + content + ", title=" + title + ", files=" + files + ", category=" + category
				+ ", comments=" + comments + ", user=" + user + "]";
	}
}