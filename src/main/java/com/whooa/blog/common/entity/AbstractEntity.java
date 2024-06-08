package com.whooa.blog.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/*
 * @MappedSuperclass 어노테이션은 부모 클래스를 테이블과 매핑하지 않고 부모 클래스를 상속받는 자식 클래스에게 매핑 정보만 제공한다.
 * 즉, 추상 클래스와 비슷하며 실제 테이블과 매핑되지 않는다.
 */
@MappedSuperclass
/*
 * @EntityListeners 어노테이션으로 리스너의 기능을 사용하면 엔티티의 생명주기에 따른 이벤트를 처리할 수 있다.
 * AuditingEntityListener 클래스는 엔티티를 저장하거나 갱신할 때 자동으로 createdAt 필드와 updatedAt 필드를 채운다.
 */
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/*
	 * @CreationTimestamp 어노테이션과 @UpdateTimestamp 어노테이션은 Hibernate가 제공한다.
	 * @CreatedDate 어노테이션과 @LastModifiedDate 어노테이션은 Spring이 제공한다.
	 * 추세는 Hibernate 어노테이션을 점점 사용하지 않는다.
	 */
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	/* 
	 * Java 1.8이상부터 Date 클래스와 Calander 클래스를 사용하지 않고 LocalDate 클래스와 LocalDateTime 클래스를 사용한다. 
	 * 이전 방식들의 문제점.
	 * 1. 그레고리력 유리우스력을 적용하여 발생한 오차날짜 생략.
	 * 2. 시간 관리(시간대 데이터베이스에서 관리, OS와 별도로 관리.)
	 * 3. 불변하지 못한 객체.
	 * 4. 상수 필드 남용.
	 * 5. 날짜 생성은 Date 클래스, 연산은 Calendar 클래스, 결과는 Date 클래스를 사용.
	 */
	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updateddAt;
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
	
	public AbstractEntity(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdateddAt() {
		return updateddAt;
	}

	public void setUpdateddAt(LocalDateTime updateddAt) {
		this.updateddAt = updateddAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	@Override
	public String toString() {
		return "BaseEntity [id=" + id + ", createdAt=" + createdAt + ", updateddAt=" + updateddAt + ", deletedAt="
				+ deletedAt + "]";
	}
}