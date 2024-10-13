package com.whooa.blog.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationParam {
	private final int PAGE_NO = 0;
	private final int PAGE_SIZE = 10;
	private final String SORT_BY = "id";
	private final String SORT_DIR = "asc";
	
	private int pageNo = PAGE_NO;
	private int pageSize = PAGE_SIZE;
	private String sortBy = SORT_BY;
	private String sortDir = SORT_DIR;
	
	public PaginationParam(int pageNo, int pageSize, String sortBy, String sortDir) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.sortBy = sortBy;
		this.sortDir = sortDir;
	}
	
	public PaginationParam() {}

	public Pageable makePageable() {
		return PageRequest.of(pageNo, pageSize, setSortDir());
	}
	
	private Sort setSortDir() {
		return this.sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(this.sortBy).ascending() : Sort.by(this.sortBy).descending();
	}
	
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

	@Override
	public String toString() {
		return "PaginationUtil [pageNo=" + pageNo + ", pageSize=" + pageSize + ", sortBy=" + sortBy + ", sortDir="
				+ sortDir + "]";
	}
}