package com.whooa.blog.file.entity;

import com.whooa.blog.common.entity.AbstractEntity;
import com.whooa.blog.post.entity.PostEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "file")
public class FileEntity extends AbstractEntity {
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String path;

	@Column(nullable = false)
	private Long size;
	
	@Column(nullable = false)
	private String ext;
	
	@Column(name = "mime_type", nullable = false)
	private String mimeType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;	
	
	// TODO: 생성자 수정.
	public FileEntity(Long id, String name, String path, Long size, String ext, String mimeType, PostEntity post) {
		super(id);
		
		this.name = name;
		this.path = path;
		this.size = size;
		this.ext = ext;
		this.mimeType = mimeType;
		this.post = post;
	}
	
	public FileEntity(String name, String path, Long size, String ext, String mimeType, PostEntity post) {
		this(-1L, name, path, size, ext, mimeType, post);
	}	
	
	public FileEntity(String name, String path, Long size, String ext, String mimeType) {
		this(name, path, size, ext, mimeType, null);
	}	
		
	public FileEntity() {
		super(-1L);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public PostEntity getPost() {
		return post;
	}

	/* 객체의 양방향 연관관계는 양쪽 모두 관계를 맺어준다. */
	public void setPost(PostEntity post) {
		/* 기존의 관계를 제거한다. */
		if (this.post != null) {
			this.post.getFiles().remove(this);
		}
		
		this.post = post;
		post.getFiles().add(this);
	}

	@Override
	public String toString() {
		return "FileEntity [name=" + name + ", path=" + path + ", size=" + size + ", ext=" + ext + ", mimeType="
				+ mimeType + ", post=" + post + "]";
	}
}