package com.whooa.blog.post.service;

import java.util.Date;

import org.springframework.data.domain.Page;

import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.param.PostSearchParam;
import com.whooa.blog.util.PaginationParam;

public interface PostElasticsearchService {
	public abstract Page<PostDoc> findAllByDate(Date startDate, Date endDate, PaginationParam paginationParam);
	public abstract Page<PostDoc> searchAll(PostSearchParam postSearchParam);
	public abstract Page<PostDoc> searchAllByDate(PostSearchParam postSearchParam, Date startDate, Date endDate);
}