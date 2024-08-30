package com.whooa.blog.post.service;

import java.util.List;

import com.whooa.blog.post.model.PostModel;

public interface PostElasticsearchService {
	public abstract List<PostModel> findByContent(String content);
	public abstract List<PostModel> findByTitle(String title);
	public abstract List<PostModel> findByCategoryName(String categoryName);
}
