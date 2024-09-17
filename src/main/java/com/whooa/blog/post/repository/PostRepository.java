package com.whooa.blog.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whooa.blog.post.entity.PostEntity;

/*
 * 1번 매개변수는 엔티티 타입, 2번 매개변수는 기본키 타입.
 * JpaRepository 인터페이스는 내부적으로 CrudRepository 인터페이스를 상속한다. CrudRepository 인터페이스는 CRUD 메서드를 제공한다.
 * SimpleJpaRepository 클래스는 JpaRepository 인터페이스를 구현한다.
 * SimpleJpaRepository 클래스가 내부적으로 @Repository 어노테이션을 사용하기 때문에 @Repository 어노테이션을 사용할 필요가 없다.
 * SimpleJpaRepository 클래스가 내부적으로 @Transactional 어노테이션을 사용하기 때문에 @Transactional 어노테이션을 사용할 필요가 없다.
 */
public interface PostRepository extends JpaRepository<PostEntity, Long> {
	public abstract Page<PostEntity> findByCategoryId(Long categoryId, Pageable pageable);
}