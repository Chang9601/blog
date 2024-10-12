package com.whooa.blog.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.whooa.blog.post.doc.PostDoc;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDoc, Long>{
	public abstract Optional<PostDoc> findById(Long id);
	public abstract List<PostDoc> findByContent(String content);
	public abstract List<PostDoc> findByTitle(String title);
	public abstract List<PostDoc> findByCategoryName(String categoryName);
}