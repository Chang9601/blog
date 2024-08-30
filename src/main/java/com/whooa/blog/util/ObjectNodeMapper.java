package com.whooa.blog.util;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ObjectNodeMapper<T> {
	public abstract T fromObjectNode(ObjectNode objectNode);
}