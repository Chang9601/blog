package com.whooa.blog.post.mapper;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.whooa.blog.post.doc.PostDoc;

public class PostObjectNodeMapper {
	public static PostDoc fromObjectNode(ObjectNode objectNode) {
		PostDoc postDoc = new PostDoc();
		
		postDoc.setCategoryName(convertEncoding(objectNode.get("category_name").asText(), "ISO-8859-1", "UTF-8"));
		postDoc.setContent(objectNode.get("content").asText());
		postDoc.setCreatedAt(LocalDate.parse(objectNode.get("created_at").asText()));
		postDoc.setId(objectNode.get("id").asLong());
		postDoc.setTitle(objectNode.get("title").asText());
	
		return postDoc;
	}
	
	public static String convertEncoding(String brokenText, String sourceEncoding, String targetEncoding) {
	    try {
	        // Convert broken string into bytes using the source encoding
	        byte[] bytes = brokenText.getBytes(sourceEncoding);
	        // Create a new string using the target encoding
	        return new String(bytes, targetEncoding);
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	        return brokenText; // return the original text in case of an error
	    }
	}
}