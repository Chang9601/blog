package com.whooa.blog.post.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.whooa.blog.post.repository.PostElasticsearchRepository;
import com.whooa.blog.post.service.PostElasticsearchService;
import com.whooa.blog.query.Index;
import com.whooa.blog.query.QueryDto;
import com.whooa.blog.query.QueryUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;

@Service
public class PostElasticsearchServiceImpl implements PostElasticsearchService {
	private PostElasticsearchRepository postElasticsearchRepository;
	private CategoryRepository categoryRepository;
	private ElasticsearchClient elasticsearchClient;
	
	public PostElasticsearchServiceImpl(PostElasticsearchRepository postElasticsearchRepository, CategoryRepository categoryRepository, ElasticsearchClient elasticsearchClient) {
		this.postElasticsearchRepository = postElasticsearchRepository;
		this.categoryRepository = categoryRepository;
		this.elasticsearchClient = elasticsearchClient;
	}
	
	@Override
	public PostDoc create(PostCreateRequest postCreate, UserDetailsImpl userDetailsImpl) {		
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

		return postDoc;
	}

	@Override
	public PostDoc find(Long id) {
		PostDoc postDocument = postElasticsearchRepository.findById(id).orElse(null);
		
		return postDocument;
	}

	@Override
	public PageResponse<PostDoc> findByContent(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageResponse<PostDoc> findByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageResponse<PostDoc> findByCategoryName(String categoryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PostDoc> search(QueryDto queryDto) {
		
		System.out.println("?????");
		
		SearchRequest searchRequest = QueryUtil.buildSearchRequest(Index.POST_INDEX, queryDto);
		
		return searchHelper(searchRequest);
	}
	
	public List<PostDoc> searchSince(Date date) {
		SearchRequest searchRequest = QueryUtil.buildSearchRequest(Index.POST_INDEX, "createdAt", date);
		
		return searchHelper(searchRequest);
	}
	
	private List<PostDoc> searchHelper(SearchRequest searchRequest) {
		if (searchRequest == null) {
			return Collections.emptyList();
		}
		
		try {
			SearchResponse searchResponse = elasticsearchClient.search(searchRequest, PostDoc.class);
			
			List<Hit<PostDoc>> searchHits = searchResponse.hits().hits();
			
			System.out.println(searchHits);
			
			List<PostDoc> posts = new ArrayList<>(searchHits.size());
			
			for (Hit<PostDoc> hit: searchHits) {
				posts.add(hit.source());
			}
			
			return posts;
			
		} catch (Exception exception) {
			return Collections.emptyList();
		}
	}
}