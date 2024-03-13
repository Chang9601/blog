package com.whooa.blog.common.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

// 부모 클래스는 테이블과 매핑하지 않으며 상속받는 자식 클래스에 매핑 정보를 상속한다.
@MappedSuperclass
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updateddAt;
	
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
