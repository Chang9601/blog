package com.whooa.blog.category.param;

import com.whooa.blog.util.PaginationParam;

public class CategorySearchParam extends PaginationParam {
	private String name;

	public CategorySearchParam() {}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CategorySearchParam [name=" + name + "]";
	}
}