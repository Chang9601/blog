package com.whooa.blog.post.entity;

import java.util.HashSet;
import java.util.Set;

import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.common.entity.AbstractEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/*
  엔티티는 반드시 매개변수가 없는 생성자와 기본 키를 가져야 한다.
  엔티티 이름은 클래스 이름으로 기본 설정된다. name을 사용하여 이름을 변경할 수 있다.
  다양한 JPA 구현체가 기능을 제공하기 위해 엔티티를 하위 클래스화(subclass)하기 때문에 엔티티 클래스는 반드시 final로 선언되어서는 안된다.
*/
@Entity
// 대부분의 경우, 데이터베이스의 테이블 이름과 엔티티의 이름은 동일하지 않다. 이러한 경우에 테이블 이름을 지정할 수 있다.
@Table(name = "post")
public class PostEntity extends AbstractEntity {
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String content;
	
	// 양방향 관계라서 @OneToMany 어노테이션을 사용한다.
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CommentEntity> comments = new HashSet<>();
	
	public PostEntity(final Long id, final String title, final String content) {
		super(id);
		
		this.title = title;
		this.content = content;
	}
	
	public PostEntity(final String title, final String content) {	
		this(-1L, title, content);
	}
	
	public PostEntity() {
		this(-1L, null, null);
	}
	
	public Long getId() {
		return super.getId();
	}
	
	public void setId(final Long id) {
		super.setId(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}
	
	public Set<CommentEntity> getComments() {
		return comments;
	}

	public void setCommentEntities(final Set<CommentEntity> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "PostEntity [id=" + super.getId() + ", title=" + title + ", content=" + content
				+ ", comments=" + comments + "]";
	}
}