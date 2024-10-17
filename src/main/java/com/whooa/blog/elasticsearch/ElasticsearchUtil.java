package com.whooa.blog.elasticsearch;

import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.whooa.blog.post.param.PostSearchParam;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;

public final class ElasticsearchUtil<T> {
	private ElasticsearchUtil() {}
		
	public static SearchRequest buildSearchRequest(String index, PostSearchParam postSearchParam) {		
		try {
			int pageNo, pageSize, from;
			SearchRequest.Builder searchRequestBuilder;
			
			pageNo = postSearchParam.getPageNo();
			pageSize = postSearchParam.getPageSize();
			from = pageNo <= 0 ? 0 : pageNo * pageSize;
			
			/* https://www.elastic.co/guide/en/elasticsearch/reference/current/filter-search-results.html */
			searchRequestBuilder = new SearchRequest.Builder()
											.index(index)
											.postFilter(buildQuery(postSearchParam))
											.from(from)
											.size(pageSize);
											//.query(buildQuery(postSearchParam));
			
			// TODO: 작동 X
			if (postSearchParam.getSortBy() != null) {
				SortOptions sortOptions = new SortOptions.Builder()
															.field(fn -> fn.field(postSearchParam.getSortBy())
															.order(postSearchParam.getSortOrder() != null ? SortOrder.valueOf(postSearchParam.getSortOrder()) : SortOrder.Asc))
															.build();
				
				searchRequestBuilder.sort(sortOptions);
			}
			
			return searchRequestBuilder.build();
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
	public static SearchRequest buildSearchRequest(String index, PostSearchParam postSearchParam, Date startDate, Date endDate) {		
		try {
			Query searchQuery, dateQuery;
			Query boolQuery;
			
			searchQuery = buildQuery(postSearchParam);
			dateQuery = buildQuery("created_at", startDate, endDate);
			
			boolQuery = new BoolQuery.Builder()
										.must(searchQuery)
										.must(dateQuery)
										.build()
										._toQuery();
						
			SearchRequest.Builder searchRequestBuilder;
			
			searchRequestBuilder = new SearchRequest.Builder()
														.index(index)
														.postFilter(boolQuery);
														//.query(boolQuery));
			
			if (postSearchParam.getSortBy() != null) {
				SortOptions sortOptions = new SortOptions.Builder()
						.field(fn -> fn.field(postSearchParam.getSortBy())
								.order(postSearchParam.getSortOrder() != null ? SortOrder.valueOf(postSearchParam.getSortOrder()) : SortOrder.Asc))
						.build();
				
				searchRequestBuilder.sort(sortOptions);
			}
			
			return searchRequestBuilder.build();
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
	public static SearchRequest buildSearchRequest(String index, String field, Date startDate, Date endDate) {
		try {
			SearchRequest searchRequest = new SearchRequest.Builder()
																.index(List.of(index))
																.postFilter(buildQuery(field, startDate, endDate))
																.build();
			
			return searchRequest;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
	// TODO: 시작/끝 날짜
	private static Query buildQuery(String field, Date startDate, Date endDate) {
		return new RangeQuery
						.Builder()
						.field(field)
						.gte(JsonData.of(startDate))
						.lte(JsonData.of(endDate))
						.build()
						._toQuery();
	}
	
	private static Query buildQuery(PostSearchParam postSearchParam) {
		List<String> fields;
		String term;
		
		if (postSearchParam == null) {
			return null;
		}
		
		fields = postSearchParam.getFields();
		term = postSearchParam.getTerm();
		
		if (CollectionUtils.isEmpty(fields)) {
			return null;
		}
		
		/* 
		 * multi_match 쿼리
		 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-match-query.html
		 */
		if (fields.size() > 1) {			
			 return new MultiMatchQuery
			 	.Builder()
				.query(term) // TODO: LIKE
				.type(TextQueryType.CrossFields)
				.operator(Operator.And)
				.fields(fields) 
				.build()
				._toQuery();
		}

		/*
		 * match 쿼리
		 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html 
		 */
		return fields.stream()
				.findFirst()
				.map((field) -> new MatchQuery.Builder()
				.query(term) // TODO: LIKE
				.operator(Operator.And)
				.field(field).build())
				.orElse(null)
				._toQuery();	
	}
}