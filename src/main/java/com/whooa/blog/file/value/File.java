package com.whooa.blog.file.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class File {
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String path;

	@Column(nullable = false)
	private Long size;
	
	@Column(nullable = false)
	private String extension;
	
	@Column(name = "mime_type", nullable = false)
	private String mimeType;
	
	// TODO: 생성자 수정.
	public File(Long id, String name, String path, Long size, String extension, String mimeType) {
		
		this.name = name;
		this.path = path;
		this.size = size;
		this.extension = extension;
		this.mimeType = mimeType;
	}
	
	public File(String name, String path, Long size, String extension, String mimeType) {
		this(-1L, name, path, size, extension, mimeType);
	}	
		
	public File() {}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public Long getSize() {
		return size;
	}

	public String getExtension() {
		return extension;
	}

	public String getMimeType() {
		return mimeType;
	}

	@Override
	public String toString() {
		return "FileEntity [name=" + name + ", path=" + path + ", size=" + size + ", extension=" + extension + ", mimeType="
				+ mimeType + "]";
	}
}