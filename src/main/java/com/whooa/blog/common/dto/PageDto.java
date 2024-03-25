package com.whooa.blog.common.dto;

import com.whooa.blog.utils.PaginationConstants;

public class PageDto {
	private int pageNo = PaginationConstants.PAGE_NO;
	private int pageSize = PaginationConstants.PAGE_SIZE;
	private String sortBy = PaginationConstants.SORT_BY;
	private String sortDir = PaginationConstants.SORT_DIR;
	
	public int getPageNo() {
		return pageNo;
	}
	
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}
}