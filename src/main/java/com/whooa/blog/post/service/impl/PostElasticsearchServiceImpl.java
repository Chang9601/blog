package com.whooa.blog.post.service.impl;

import java.util.List;

import com.whooa.blog.post.model.PostModel;
import com.whooa.blog.post.repository.PostElasticsearchRepository;
import com.whooa.blog.post.service.PostElasticsearchService;

public class PostElasticsearchServiceImpl implements PostElasticsearchService {
	
	private PostElasticsearchRepository postElasticsearchRepository;
	
	public PostElasticsearchServiceImpl(PostElasticsearchRepository postElasticsearchRepository) {
		this.postElasticsearchRepository = postElasticsearchRepository;
	}
	
	@Override
	public List<PostModel> findByContent(String content) {
		return postElasticsearchRepository.findByContent(content);
	}

	@Override
	public List<PostModel> findByTitle(String title) {
		return postElasticsearchRepository.findByTitle(title);

	}

	@Override
	public List<PostModel> findByCategoryName(String categoryName) {
		return postElasticsearchRepository.findByCategoryName(categoryName);
	}
}