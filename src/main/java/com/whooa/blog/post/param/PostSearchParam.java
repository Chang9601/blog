package com.whooa.blog.post.param;

import java.util.List;

import com.whooa.blog.util.PaginationParam;

import co.elastic.clients.elasticsearch._types.SortOrder;

public class PostSearchParam extends PaginationParam {
	private List<String> fields;
	private String term;
	private String sortBy;
	private String sortOrder;
	
	public PostSearchParam() {}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "PostSearchParam [fields=" + fields + ", term=" + term + ", sortBy=" + sortBy + ", sortOrder="
				+ sortOrder + "]";
	}
}