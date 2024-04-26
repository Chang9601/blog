package com.whooa.blog.file.dto;

public class FileDto {
	private Long id;
	private String name;
	private String path;
	private Long size;
	private String ext;
	private String mimeType;
	
	public FileDto(Long id, String name, String path, Long size, String ext, String mimeType) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.size = size;
		this.ext = ext;
		this.mimeType = mimeType;
	}
	
	public FileDto () {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "FileDto [id=" + id + ", name=" + name + ", path=" + path + ", size=" + size + ", ext=" + ext
				+ ", mimeType=" + mimeType + "]";
	}
}