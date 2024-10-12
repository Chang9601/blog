package com.whooa.blog.elasticsearch;

import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
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
		
	public static SearchRequest buildSearchRequest(String index, ElasticsearchParam elasticsearchParam) {		
		try {
			SearchRequest.Builder searchRequestBuilder;
			
			searchRequestBuilder = new SearchRequest.Builder()
											.index(index)
											.postFilter(buildQuery(elasticsearchParam));
											//.query(buildQuery(elasticsearchParam));
			
			if (elasticsearchParam.getSortBy() != null) {
				SortOptions sortOptions = new SortOptions.Builder()
					.field(fn -> fn.field(elasticsearchParam.getSortBy())
					.order(elasticsearchParam.getSortOrder() != null ? elasticsearchParam.getSortOrder() : SortOrder.Asc))
					.build();
				
				searchRequestBuilder.sort(sortOptions);
			}
			
			return searchRequestBuilder.build();
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
	public static SearchRequest buildSearchRequest(String index, String field, Date date) {
		try {
			SearchRequest searchRequest = new SearchRequest.Builder()
				.postFilter(buildRangeQuery(field, date))
				.index(List.of(index))
				.build();
			
			return searchRequest;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
		
	}
	
	private static Query buildRangeQuery(String field, Date date) {
		return new RangeQuery
			.Builder()
			.field(field)
			.gte(JsonData.of(date))
			.build()
			._toQuery();
	}
	
	private static Query buildQuery(ElasticsearchParam elasticsearchDto) {
		List<String> fields;
		String searchTerm;
		
		if (elasticsearchDto == null) {
			return null;
		}
		
		fields = elasticsearchDto.getFields();
		searchTerm = elasticsearchDto.getSearchTerm();
		
		if (CollectionUtils.isEmpty(fields)) {
			return null;
		}
		
		if (fields.size() > 1) {			
			 return new MultiMatchQuery
			 	.Builder()
				.query(searchTerm)
				.type(TextQueryType.CrossFields)
				.operator(Operator.And)
				.fields(fields)
				.build()
				._toQuery();
		}

		return fields.stream()
				.findFirst()
				.map((field) -> new MatchQuery.Builder()
				.query(searchTerm)
				.operator(Operator.And)
				.field(field).build())
				.orElse(null)
				._toQuery();	
	}
}