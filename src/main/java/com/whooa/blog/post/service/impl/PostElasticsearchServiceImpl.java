package com.whooa.blog.post.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.elasticsearch.client.RequestOptions;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import com.whooa.blog.elasticsearch.ElasticsearchIndex;
import com.whooa.blog.elasticsearch.ElasticsearchParam;
import com.whooa.blog.elasticsearch.ElasticsearchUtil;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.repository.PostElasticsearchRepository;
import com.whooa.blog.post.service.PostElasticsearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class PostElasticsearchServiceImpl implements PostElasticsearchService {
	private PostElasticsearchRepository postElasticsearchRepository;
	private ElasticsearchClient elasticsearchClient;
	private ElasticsearchOperations elasticsearchOperations;

	public PostElasticsearchServiceImpl(PostElasticsearchRepository postElasticsearchRepository,
			ElasticsearchClient elasticsearchClient,
			ElasticsearchOperations elasticsearchOperations) {
		this.postElasticsearchRepository = postElasticsearchRepository;
		this.elasticsearchClient = elasticsearchClient;
		this.elasticsearchOperations = elasticsearchOperations;
	}
	
	@Override
	public List<PostDoc> search(ElasticsearchParam elasticsearchParam) {
		System.out.println(elasticsearchParam);
		
		SearchRequest searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, elasticsearchParam);
		
		return searchHelper(searchRequest);
	}
	
	public List<PostDoc> searchSince(Date date) {
		SearchRequest searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, "createdAt", date);
		
		return searchHelper(searchRequest);
	}
	
	private List<PostDoc> searchHelper(SearchRequest searchRequest) {
		if (searchRequest == null) {
			return Collections.emptyList();
		}
		
		System.out.println(searchRequest);
		
		try {			
			SearchResponse<PostDoc> searchResponse = elasticsearchClient.search(searchRequest, PostDoc.class);
			
			System.out.println(searchResponse);
//			elasticsearchClient.
//			
//			List<Hit<PostDoc>> searchHits = searchResponse.hits().hits();
//			
//			System.out.println(searchHits);
//			System.out.println(searchResponse);
//
			List<PostDoc> posts = new ArrayList<>();
//			
			for (Hit<PostDoc> hit: searchResponse.hits().hits()) {
				posts.add(hit.source());
			}
			
			System.out.println(posts);
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			return null;
			
		} catch (Exception exception) {
			return Collections.emptyList();
		}
	}
}