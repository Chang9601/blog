package com.whooa.blog.post.service;

import java.util.Date;
import java.util.List;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.query.QueryDto;

public interface PostElasticsearchService {
	public abstract PostDoc create(PostCreateRequest postCreate, UserDetailsImpl userDetailsImpl);
	public abstract PostDoc find(Long id);
	public abstract PageResponse<PostDoc> findByContent(String content);
	public abstract PageResponse<PostDoc> findByTitle(String title);
	public abstract PageResponse<PostDoc> findByCategoryName(String categoryName);
	public abstract List<PostDoc> search(QueryDto queryDto);
	public abstract List<PostDoc> searchSince(Date date);
}