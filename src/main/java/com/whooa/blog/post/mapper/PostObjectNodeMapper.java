package com.whooa.blog.post.mapper;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.whooa.blog.post.doc.PostDoc;

public class PostObjectNodeMapper {
	public static PostDoc fromObjectNode(ObjectNode objectNode) {
		PostDoc postDoc = new PostDoc();
		
		postDoc.setCategoryName((objectNode.get("category_name").asText()));
		postDoc.setContent(objectNode.get("content").asText());
		postDoc.setCreatedAt(LocalDate.parse(objectNode.get("created_at").asText()));
		postDoc.setId(objectNode.get("id").asLong());
		postDoc.setTitle(objectNode.get("title").asText());
	
		return postDoc;
	}
}