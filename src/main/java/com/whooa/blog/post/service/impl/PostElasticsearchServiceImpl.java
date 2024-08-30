package com.whooa.blog.post.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.whooa.blog.elasticsearch.ElasticsearchIndex;
import com.whooa.blog.elasticsearch.ElasticsearchUtil;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.mapper.PostObjectNodeMapper;
import com.whooa.blog.post.param.PostSearchParam;
import com.whooa.blog.post.service.PostElasticsearchService;
import com.whooa.blog.util.PaginationParam;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;

@Service
public class PostElasticsearchServiceImpl implements PostElasticsearchService {
	private ElasticsearchClient elasticsearchClient;

	public PostElasticsearchServiceImpl(ElasticsearchClient elasticsearchClient) {
		this.elasticsearchClient = elasticsearchClient;
	}
	
	public Page<PostDoc> findAllByDate(Date startDate, Date endDate, PaginationParam paginationParam) {
		SearchRequest searchRequest;
		Pageable pageable;
		
		pageable = paginationParam.makePageable();
		searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, "created_at", startDate, endDate, paginationParam);
		
		return performSearch(searchRequest, pageable);
	}
	
	@Override
	public Page<PostDoc> searchAll(PostSearchParam postSearchParam) {		
		SearchRequest searchRequest;
		Pageable pageable;

		pageable = postSearchParam.makePageable();
		searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, postSearchParam);
		
		return performSearch(searchRequest, pageable);
	}
	
	@Override
	public Page<PostDoc> searchAllByDate(PostSearchParam postSearchParam, Date startDate, Date endDate) {
		SearchRequest searchRequest;
		Pageable pageable;

		pageable = postSearchParam.makePageable();
		searchRequest = ElasticsearchUtil.buildSearchRequest(ElasticsearchIndex.POST_INDEX, postSearchParam, startDate, endDate);
				
		return performSearch(searchRequest, pageable);
	}
	
	private Page<PostDoc> performSearch(SearchRequest searchRequest, Pageable pageable) {
		HitsMetadata<ObjectNode> hitsMetadata;
		List<Hit<ObjectNode>> objectNodeHits;
		List<ObjectNode> objectNodes;
		List<PostDoc> posts;
		SearchResponse<ObjectNode> searchResponse;
		long total;
		TotalHits totalHits;
		
		if (searchRequest == null) {
			return new PageImpl<PostDoc>(Collections.emptyList(), pageable, 0);
		}
				
		try {
			// TODO: PostDoc.class -> co.elastic.clients.transport.TransportException: node: https://localhost:9200/, status: 200, [es/search] Failed to decode response
			searchResponse = elasticsearchClient.search(searchRequest, ObjectNode.class);
									
			hitsMetadata = searchResponse.hits();
			
			totalHits = hitsMetadata.total();
			objectNodeHits = hitsMetadata.hits();
			
			objectNodes = objectNodeHits.stream()
											.map(objectNodeHit -> objectNodeHit.source())
											.collect(Collectors.toList());

			posts = objectNodes.stream()
									.map(objectNode -> PostObjectNodeMapper.fromObjectNode(objectNode))
									.collect(Collectors.toList());
			
			total = totalHits.value();
						
			return new PageImpl<PostDoc>(posts, pageable, total);
			
		} catch (Exception exception) {
			System.out.println(exception);
			
			return new PageImpl<PostDoc>(Collections.emptyList(), pageable, 0);
		}
	}
}