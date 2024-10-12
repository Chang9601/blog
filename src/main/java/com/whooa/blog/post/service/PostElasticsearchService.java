package com.whooa.blog.post.service;

import java.util.Date;
import java.util.List;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.elasticsearch.ElasticsearchParam;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;

public interface PostElasticsearchService {
	public abstract List<PostDoc> search(ElasticsearchParam elasticsearchParam);
	public abstract List<PostDoc> searchSince(Date date);
}