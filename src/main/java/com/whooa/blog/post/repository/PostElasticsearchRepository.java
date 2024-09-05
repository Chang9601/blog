//package com.whooa.blog.post.repository;
//
//import java.util.List;
//
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import com.whooa.blog.post.model.PostModel;
//
//public interface PostElasticsearchRepository extends ElasticsearchRepository<PostModel, String>{
//	public abstract List<PostModel> findByContent(String content);
//	public abstract List<PostModel> findByTitle(String title);
//	public abstract List<PostModel> findByCategoryName(String categoryName);
//}