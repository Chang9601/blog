package com.whooa.blog.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.file.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

}