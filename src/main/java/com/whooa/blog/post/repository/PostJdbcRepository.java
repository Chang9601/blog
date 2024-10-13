package com.whooa.blog.post.repository;

import java.util.List;

import com.whooa.blog.file.value.File;

public interface PostJdbcRepository {
	public abstract void bulkInsert(Long postId, List<File> files);
}