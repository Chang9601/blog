package com.whooa.blog.post.entity;

import com.whooa.blog.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// 엔티티는 반드시 매개변수가 없는 생성자와 기본 키를 가져야 한다.
// 엔티티 이름은 클래스 이름으로 기본 설정된다. name을 사용하여 이름을 변경할 수 있다.
// 다양한 JPA 구현체가 기능을 제공하기 위해 엔티티를 하위 클래스화(subclass)하기 때문에 엔티티 클래스는 반드시 final로 선언되어서는 안된다.
@Entity
// 대부분의 경우, 데이터베이스의 테이블 이름과 엔티티의 이름은 동일하지 않다. 이러한 경우에 테이블 이름을 지정할 수 있다.
@Table(name = "post")
public class Post extends BaseEntity {

	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false)
	private String content;
}