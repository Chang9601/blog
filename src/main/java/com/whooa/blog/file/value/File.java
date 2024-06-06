package com.whooa.blog.file.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class File {
	@Column(nullable = false)
	private String extension;
	
	@Column(name = "mime_type", nullable = false)
	private String mimeType;
	
	@Column(length = 500, nullable = false)
	private String name;
	
	@Column(length = 700, nullable = false)
	private String path;

	@Column(nullable = false)
	private Long size;
	
	public File(String extension, String mimeType, String name, String path, Long size) {
		this.extension = extension;
		this.mimeType = mimeType;
		this.name = name;
		this.path = path;
		this.size = size;
	}

	public File() {}
	
	public String getExtension() {
		return extension;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public Long getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "File [extension=" + extension + ", mimeType=" + mimeType + ", name=" + name + ", path=" + path
				+ ", size=" + size + "]";
	}
}