package com.whooa.blog.common.dto;

import com.whooa.blog.utils.PaginationConstants;

public class PageDto {
	private int pageNo = PaginationConstants.PAGE_NO;
	private int pageSize = PaginationConstants.PAGE_SIZE;
	private String sortBy = PaginationConstants.SORT_BY;
	private String sortDir = PaginationConstants.SORT_DIR;
	
	public PageDto() {}
	
	public PageDto(final int pageNo, final int pageSize, final String sortBy, final String sortDir) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.sortBy = sortBy;
		this.sortDir = sortDir;
	}

	public int getPageNo() {
		return pageNo;
	}
	
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(final String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(final String sortDir) {
		this.sortDir = sortDir;
	}

	@Override
	public String toString() {
		return "PageDto [pageNo=" + pageNo + ", pageSize=" + pageSize + ", sortBy=" + sortBy + ", sortDir=" + sortDir
				+ "]";
	}
}