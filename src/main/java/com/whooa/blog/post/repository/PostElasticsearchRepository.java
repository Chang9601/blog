package com.whooa.blog.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.whooa.blog.post.document.PostDocument;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, Long>{
	public abstract Optional<PostDocument> findById(Long id);
	public abstract List<PostDocument> findByContent(String content);
	public abstract List<PostDocument> findByTitle(String title);
	public abstract List<PostDocument> findByCategoryName(String categoryName);
}