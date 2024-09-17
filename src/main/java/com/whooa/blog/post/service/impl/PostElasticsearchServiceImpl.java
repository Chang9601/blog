package com.whooa.blog.post.service.impl;

import org.springframework.stereotype.Service;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.doc.PostDoc.PostDocBuilder;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostElasticsearchRepository;
import com.whooa.blog.post.service.PostElasticsearchService;

@Service
public class PostElasticsearchServiceImpl implements PostElasticsearchService {
	private PostElasticsearchRepository postElasticsearchRepository;
	private CategoryRepository categoryRepository;
	
	public PostElasticsearchServiceImpl(PostElasticsearchRepository postElasticsearchRepository, CategoryRepository categoryRepository) {
		this.postElasticsearchRepository = postElasticsearchRepository;
		this.categoryRepository = categoryRepository;
	}
	
	@Override
	public PostResponse create(PostCreateRequest postCreate, UserDetailsImpl userDetailsImpl) {
		Long categoryId, userId;
		String categoryName, content, title;
		
		categoryName = postCreate.getCategoryName();
		content = postCreate.getContent();
		title = postCreate.getTitle();
		
		CategoryEntity categoryEntity = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		categoryId = categoryEntity.getId();
		userId = userDetailsImpl.getId();
		
		PostDocBuilder postDocBuilder = PostDoc.builder();	
		PostDoc postDoc = postDocBuilder
							.categoryName(categoryName)
							.content(content)
							.title(title)
							.categoryId(categoryId)
							.userId(userId)
							.build();
	
		PostResponse post = PostMapper.INSTANCE.toDto(postElasticsearchRepository.save(postDoc));
		
		return post;
	}

	@Override
	public PostResponse find(Long id) {
		PostDoc postDocument = postElasticsearchRepository.findById(id).orElse(null);
		
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