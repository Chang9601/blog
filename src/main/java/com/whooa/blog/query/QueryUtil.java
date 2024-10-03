package com.whooa.blog.query;

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

public final class QueryUtil {
	
	private QueryUtil() {}
	
	public static SearchRequest buildSearchRequest(String index, QueryDto queryDto) {		
		try {
			SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
				.index(List.of(index))
				.query(buildQuery(queryDto));
			
			if (queryDto.getSortBy() != null) {
				SortOptions sortOptions = new SortOptions.Builder()
					.field(fn -> fn.field(queryDto.getSortBy())
					.order(queryDto.getSortOrder() != null ? queryDto.getSortOrder() : SortOrder.Asc))
					.build();
				
				searchRequestBuilder.sort(sortOptions);
			}
			
			SearchRequest searchRequest = searchRequestBuilder.build();

			return searchRequest;
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
	
	public static Query buildRangeQuery(String field, Date date) {
		return new RangeQuery
			.Builder()
			.field(field)
			.gte(JsonData.of(date))
			.build()
			._toQuery();
	}
	
	public static Query buildQuery(QueryDto queryDto) {
		List<String> fields;
		String query;
		
		if (queryDto == null) {
			return null;
		}
		
		fields = queryDto.getFields();
		query = queryDto.getQuery();
		
		if (CollectionUtils.isEmpty(fields)) {
			return null;
		}
		
		if (fields.size() > 1) {
			 return new MultiMatchQuery
			 	.Builder()
				.query(query)
				.type(TextQueryType.CrossFields)
				.operator(Operator.And)
				.fields(fields)
				.build()
				._toQuery();						
		}

		return fields.stream()
			.findFirst()
			.map((field) -> new MatchQuery.Builder()
			.query(query)
			.operator(Operator.And)
			.field(field))
			.orElse(null)
			.build()
			._toQuery();	
	}
}