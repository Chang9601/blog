package com.whooa.blog.post.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.whooa.blog.elasticsearch.ElasticsearchIndex;
import com.whooa.blog.elasticsearch.ElasticsearchUtil;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.param.PostSearchParam;
import com.whooa.blog.post.service.PostElasticsearchService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;

@Service
public class PostElasticsearchServiceImpl implements PostElasticsearchService {
//	private PostElasticsearchRepository postElasticsearchRepository;
	private ElasticsearchClient elasticsearchClient;

	public PostElasticsearchServiceImpl(ElasticsearchClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}
	
	public List<PostDoc> findAllByDate(Date startDate, Date endDate) {
		SearchRequest searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, "created_at", startDate, endDate);
		
		return performSearch(searchRequest);
	}
	
	@Override
	public List<PostDoc> searchAll(PostSearchParam postSearchParam) {		
		SearchRequest searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, postSearchParam);
		
		return performSearch(searchRequest);
	}
	
	@Override
	public List<PostDoc> searchAllByDate(PostSearchParam postSearchParam, Date startDate, Date endDate) {
		SearchRequest searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, postSearchParam, startDate, endDate);
				
		return performSearch(searchRequest);
	}
	
	private List<PostDoc> performSearch(SearchRequest searchRequest) {
		if (searchRequest == null) {
			return Collections.emptyList();
		}
				
		try {
			// TODO: PostDoc.class -> co.elastic.clients.transport.TransportException: node: https://localhost:9200/, status: 200, [es/search] Failed to decode response
			SearchResponse<ObjectNode> searchResponse = elasticsearchClient.search(searchRequest, ObjectNode.class);
						
			HitsMetadata<ObjectNode> hitsMetadata = searchResponse.hits();
			
			TotalHits totalHits = hitsMetadata.total();
			List<Hit<ObjectNode>> objectNodeHits = hitsMetadata.hits();
			
			System.out.println(totalHits);
			System.out.println(objectNodeHits);
			
			List<ObjectNode> objectNodes = objectNodeHits.stream().map(objectNode -> objectNode.source()).collect(Collectors.toList());
			
			
//			elasticsearchClient.
//			
//			List<Hit<PostDoc>> searchHits = searchResponse.hits().hits();
//			
//			System.out.println(searchHits);
//			System.out.println(searchResponse);
//
			List<PostDoc> posts = new ArrayList<>();
//			
//			for (Hit<PostDoc> hit: searchResponse.hits().hits()) {
//				posts.add(hit.source());
//			}
			
			System.out.println(posts);
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			return posts;
			
		} catch (Exception exception) {
			System.out.println(exception);
			
			return Collections.emptyList();
		}
	}
}