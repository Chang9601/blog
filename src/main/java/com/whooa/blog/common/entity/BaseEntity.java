package com.whooa.blog.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

// 부모 클래스는 테이블과 매핑하지 않으며 상속받는 자식 클래스에 매핑 정보를 상속한다.
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updateddAt;
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
