package com.whooa.blog.post.service.impl;

import com.whooa.blog.category.service.CategoryService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.document.PostDocument;
import com.whooa.blog.post.document.PostDocument.PostDocumentBuilder;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostElasticsearchRepository;
import com.whooa.blog.post.service.PostElasticsearchService;

public class PostElasticsearchServiceImpl implements PostElasticsearchService {
	private PostElasticsearchRepository postElasticsearchRepository;
	private CategoryService categoryService;
	
	public PostElasticsearchServiceImpl(PostElasticsearchRepository postElasticsearchRepository, CategoryService categoryService) {
		this.postElasticsearchRepository = postElasticsearchRepository;
		this.categoryService = categoryService;
	}
	
	@Override
	public PostResponse create(PostCreateRequest postCreate, UserDetailsImpl userDetailsImpl) {
		Long categoryId, userId;
		String content, title;
		
		PostDocumentBuilder postDocumentBuilder = PostDocument.builder();	
		PostDocument postDocument = postDocumentBuilder.content(null).title(null).categoryId(null).build();
	
		PostResponse post = PostMapper.INSTANCE.toDto(postElasticsearchRepository.save(postDocument));
		
		return post;
	}

	@Override
	public PostResponse find(Long id) {
		PostDocument postDocument = postElasticsearchRepository.findById(id).orElse(null);
		
		return PostMapper.INSTANCE.toDto(postDocument);
	}

	@Override
	public PageResponse<PostResponse> findByContent(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageResponse<PostResponse> findByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageResponse<PostResponse> findByCategoryName(String categoryName) {
		// TODO Auto-generated method stub
		return null;
	}
}