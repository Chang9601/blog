package com.whooa.blog.elasticsearch;

import java.util.List;

import co.elastic.clients.elasticsearch._types.SortOrder;

public class ElasticsearchParam {
	private List<String> fields;
	private String searchTerm;
	private String sortBy;
	private SortOrder sortOrder;
	
	public List<String> getFields() {
		return fields;
	}
	
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "ElasticsearchParam [fields=" + fields + ", searchTerm=" + searchTerm + ", sortBy=" + sortBy
				+ ", sortOrder=" + sortOrder + "]";
	}
}