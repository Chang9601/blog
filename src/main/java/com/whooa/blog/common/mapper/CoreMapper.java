package com.whooa.blog.common.mapper;

public abstract class CoreMapper<D, E> {
	public abstract D toDto(E entity);
	public abstract E toEntity(D dto);
}