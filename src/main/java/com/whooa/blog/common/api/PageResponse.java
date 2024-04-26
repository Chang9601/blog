package com.whooa.blog.common.api;

import java.util.List;

public class PageResponse<T> {
	private List<T> content;
	private int pageSize;
	private int pageNo;
	private long totalElements;
	private int totalPages;
	private boolean isLast;
	private boolean isFirst;
	
	private PageResponse(List<T> content, int pageSize, int pageNo, long totalElements, int totalPages, boolean isLast,
			boolean isFirst) {
		this.content = content;
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.isLast = isLast;
		this.isFirst = isFirst;
	}

	public static <T> PageResponse<T> handleResponse(List<T> content, int pageSize, int pageNo, long totalElements, int totalPages, boolean isLast,
			 boolean isFirst) {
		return new PageResponse<>(content, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	public List<T> getContent() {
		return content;
	}	

	public int getPageSize() {
		return pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public boolean isLast() {
		return isLast;
	}

	public boolean isFirst() {
		return isFirst;
	}

	@Override
	public String toString() {
		return "PageResponse [content=" + content + ", pageSize=" + pageSize + ", pageNo=" + pageNo + ", totalElements="
				+ totalElements + ", totalPages=" + totalPages + ", isLast=" + isLast + ", isFirst=" + isFirst + "]";
	}
}