package com.whooa.blog.common.doc;

import org.springframework.data.annotation.Id;

public abstract class CoreDoc {
	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
}