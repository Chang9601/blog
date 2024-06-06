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
		return new PageResponse<T>(content, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	@Override
	public String toString() {
		return "PageResponse [content=" + content + ", pageSize=" + pageSize + ", pageNo=" + pageNo + ", totalElements="
				+ totalElements + ", totalPages=" + totalPages + ", isLast=" + isLast + ", isFirst=" + isFirst + "]";
	}
}