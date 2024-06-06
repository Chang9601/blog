package com.whooa.blog.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.category.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>  {

}