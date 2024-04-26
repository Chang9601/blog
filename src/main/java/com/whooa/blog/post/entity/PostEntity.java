package com.whooa.blog.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.common.entity.AbstractEntity;
import com.whooa.blog.file.entity.FileEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/*
 * 엔티티는 반드시 매개변수가 없는 생성자와 기본 키를 가져야 한다.
 * 엔티티 이름은 클래스 이름으로 기본 설정된다. name 속성을 사용하여 이름을 변경할 수 있다.
 * 다양한 JPA 구현체가 기능을 제공하기 위해 엔티티를 하위 클래스화(subclass)하기 때문에 엔티티 클래스는 반드시 로 선언되어서는 안된다.
 */
@Entity
/* 
 * 대부분의 경우, 데이터베이스의 테이블 이름과 엔티티의 이름은 동일하지 않다. 
 * 이러한 경우에 테이블 이름을 지정할 수 있다. 
 */
@Table(name = "post")
public class PostEntity extends AbstractEntity {
	@Column(length = 300, nullable = false)
	private String title;
	
	@Column(length = 2000, nullable = false)
	private String content;
	
	/*
	 * 양방향 관계라서 @OneToMany 어노테이션을 사용한다.
	 * 
	 * 영속성 전이(cascading)란 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만드는 것을 의미한다.
	 * 즉, 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장할 수 있다.
	 * 주의할 점은 JPA의 경우 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 하는데 영속성 전이를 사용하면 부모 엔티티만 영속 상태로 만들면 연관된 자식 엔티티까지 한 번에 영속 상태로 만들 수 있다.
	 * 삭제 시 영속성 전이를 사용하면 부모 엔티티 삭제 시 연관된 자식 엔티티도 함께 삭제한다. 삭제 순서는 외래 키 제약조건을 고려해서 자식 엔티티를 먼저 삭제하고 부모 엔티티를 삭제한다.
   *
	 * CascadeType.PERSIST는 save() 메서드 및 persist() 메서드 작업이 자식 엔티티에 대해 계층적으로 적용된다.
	 * CascadeType.MERGE는 부모 엔티티가 병합될 때 자식 엔티티도 함께 병합된다.
	 * CascadeType.REFRESH는 refresh() 메서드 작업과 동일한 작업을 수행한다.
	 * CascadeType.REMOVE는 부모 엔티티가 삭제될 때 관련된 모든 자식 엔티티가 제거된다.
	 * CascadeType.DETACHT는 부모 엔티티의 수동 분리가 발생하면 모든 자식 엔티티가 분리된다.
	 * CascadeType.ALL는 모든 영속성 전이 작업에 대한 약칭이다.
	 *
	 * 고아 객체 제거는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능이다.
	 * 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제된다. 
	 * 또한, 엔티티를 제거하면 데이터베이스의 데이터도 자동으로 삭제된다.
	 * 특정 엔티티가 개인 소유하는 엔티티에만 이 기능을 사용해야 하는데 이런 이유로 @OneToOne과 @OneToMany에만 사용할 수 있다.
	 * 개념적으로 볼 때 부모 엔티티를 제거하면 자식 엔티티는 고아가 되므로 부모 엔티티를 제거하면 자식 엔티티도 같이 제거된다.
	 * 이는 CascadeType.REMOVE를 설정한 것과 같다.
	 * 
	 * Cascade.ALL + orphanRemoval = true
	 * 부모 엔티티를 통해서 자식 엔티티의 생명 주기를 관리한다.
	 * 자식 엔티티를 저장하라면 부모 엔티티만 등록하고 자식 엔티티를 삭제하려면 부모 엔티티에서 제거한다.
	 */
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentEntity> comments = new ArrayList<CommentEntity>();
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FileEntity> files = new ArrayList<FileEntity>();	

	/*
	 * List, Set과 같은 자료구조는 엔티티 생성 시 바로 초기화를 하기 때문에 생성자에 포함하지 않는다.
	 * 포함하면 게터에서 NullPointerException이 발생한다. 
	 */
	public PostEntity(Long id, String title, String content) {
		super(id);
		
		this.title = title;
		this.content = content;
	}

	public PostEntity(String title, String content) {	
		this(-1L, title, content);
	}
	
	public PostEntity() {
		super(-1L);
	}
	
	public Long getId() {
		return super.getId();
	}
	
	public void setId(Long id) {
		super.setId(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public List<CommentEntity> getComments() {
		return comments;
	}

	public void setComments(List<CommentEntity> comments) {
		this.comments = comments;
	}

	public List<FileEntity> getFiles() {
		return files;
	}

	public void setFiles(List<FileEntity> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "PostEntity [id=" + super.getId() + ", title=" + title + ", content=" + content
				+ ", comments=" + comments + ", files=" + files + "]";
	}
}