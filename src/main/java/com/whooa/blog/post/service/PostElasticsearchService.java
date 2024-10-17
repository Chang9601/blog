package com.whooa.blog.post.service;

import java.util.Date;
import java.util.List;

import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.param.PostSearchParam;

public interface PostElasticsearchService {
	public abstract List<PostDoc> findAllByDate(Date startDate, Date endDate);
	public abstract List<PostDoc> searchAll(PostSearchParam postSearchParam);
	public abstract List<PostDoc> searchAllByDate(PostSearchParam postSearchParam, Date startDate, Date endDate);
}