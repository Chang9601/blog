package com.whooa.blog.category.entity;

import java.util.ArrayList;
import java.util.List;

import com.whooa.blog.common.entity.CoreEntity;
import com.whooa.blog.post.entity.PostEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class CategoryEntity extends CoreEntity {
	@Column(length = 300, nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "category")
	private List<PostEntity> posts = new ArrayList<PostEntity>();
	
	public CategoryEntity() {
		super(0L);
	}
	
	public Long getId() {
		return super.getId();
	}
	
	public void setId(Long id) {
		super.setId(id);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PostEntity> getPosts() {
		return posts;
	}

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		return "CategoryEntity [id=" + super.getId() + ", name=" + name + ", posts=" + posts + "]";
	}
	
	public static CategoryEntityBuilder builder() {
		return new CategoryEntityBuilder();
	}
	
	public static final class CategoryEntityBuilder {
		private Long id;
		private String name;
		private List<PostEntity> posts;		
		
		private CategoryEntityBuilder() {}
		
		public CategoryEntityBuilder id(Long id) {
			this.id = id;
			return this;
		}
		
		public CategoryEntityBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public CategoryEntityBuilder posts(List<PostEntity> posts) {
			this.posts = posts;
			return this;
		}
		
		public CategoryEntity build() {
			CategoryEntity categoryEntity = new CategoryEntity();
			
			if (id != null) {
				categoryEntity.setId(id);
			}
			
			categoryEntity.setName(name);
			
			if (posts != null) {
				categoryEntity.setPosts(posts);
			}
			
			return categoryEntity;
		}
	}
}