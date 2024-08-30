package com.whooa.blog.elasticsearch;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.doc.CoreDoc;

@Component
public class ElasticsearchOperationsUtil<T extends CoreDoc> {
	private ElasticsearchOperations elasticsearchOperations;
	
	public ElasticsearchOperationsUtil(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}
	
	public void create(T doc) {
		elasticsearchOperations.save(doc);
	}
	
	public T find(String id, Class<T> clazz) {
		return elasticsearchOperations.get(id, clazz);
	}
	
	public void delete(T doc) {
		elasticsearchOperations.delete(doc);
	}
	
	public void update(T doc) {
		elasticsearchOperations.update(doc);
	}
}