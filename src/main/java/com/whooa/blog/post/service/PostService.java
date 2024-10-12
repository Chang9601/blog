package com.whooa.blog.post.service;

import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.elasticsearch.ElasticsearchParam;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.post.dto.PostDto.PostResponse;

public interface PostService {
	/* 인터페이스의 메서드는 기본적으로 public 접근 제한자를 사용하고 정적이다. */
	public abstract PostResponse create(PostCreateRequest postCreate, MultipartFile[] uploadFiles, UserDetailsImpl userDetailsImpl);
	public abstract void delete(Long id, UserDetailsImpl userDetailsImpl);
	public abstract PostResponse find(Long id);
	public abstract PageResponse<PostResponse> findAll(PaginationUtil paginationUtil);
	public abstract PageResponse<PostResponse> findAllByCategoryId(Long categoryId, PaginationUtil paginationUtil);
	public abstract PageResponse<PostResponse> search(ElasticsearchParam elasticsearchParam);
	public abstract PostResponse update(Long id, PostUpdateRequest postUpdate, MultipartFile[] uploadFiles, UserDetailsImpl userDetailsImpl);
}