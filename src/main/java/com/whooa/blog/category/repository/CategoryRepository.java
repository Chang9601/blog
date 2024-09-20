package com.whooa.blog.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.category.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>  {
	public abstract Boolean existsByName(String email);
	public abstract Optional<CategoryEntity> findByName(String name);
}