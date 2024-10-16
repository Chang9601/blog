package com.whooa.blog.elasticsearch;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.doc.CoreDoc;

@Component
public class ElasticsearchOperationsUtil<T extends CoreDoc> {
	private final ElasticsearchOperations elasticsearchOperations;

	public ElasticsearchOperationsUtil(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
	}
	
	public void create(T doc) {
		elasticsearchOperations.save(doc);
	}
	
	public void update(T doc) {
		Document foundDoc = elasticsearchOperations.getElasticsearchConverter().mapObject(doc);
		UpdateQuery updateQuery = UpdateQuery.builder(foundDoc.getId())
									.withDocument(foundDoc)
									.withDocAsUpsert(true)
									.build();
		
		elasticsearchOperations.update(updateQuery, IndexCoordinates.of(foundDoc.getIndex()));
	}
	
	public void delete(T doc) {
		Document foundDoc = elasticsearchOperations.getElasticsearchConverter().mapObject(doc);
		elasticsearchOperations.delete(foundDoc);
	}
}