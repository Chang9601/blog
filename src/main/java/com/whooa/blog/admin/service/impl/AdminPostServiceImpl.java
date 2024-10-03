package com.whooa.blog.admin.service.impl;

import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.admin.service.AdminPostService;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.util.StringUtil;

@Service
public class AdminPostServiceImpl implements AdminPostService {
	private PostRepository postRepository;
	private CategoryRepository categoryRepository;
	private FileService fileService;

	public AdminPostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository,
			FileService fileService) {
		this.postRepository = postRepository;
		this.categoryRepository = categoryRepository;
		this.fileService = fileService;
	}

	@Override
	public void delete(Long id) {
		PostEntity postEntity;
		
		postEntity = find(id);
				
		postRepository.delete(postEntity);
	}

	@Override
	public PostResponse update(Long id, PostUpdateRequest postUpdate, MultipartFile[] uploadFiles) {
		CategoryEntity categoryEntity;
		String categoryName, content, title;
		List<File> files = null;
		PostResponse post;
		PostEntity postEntity;
		
		postEntity = find(id);		
		categoryEntity = categoryRepository.findByName(postUpdate.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		categoryName = postUpdate.getCategoryName();
		content = postUpdate.getTitle();
		title = postUpdate.getContent();

		if (StringUtil.notEmpty(categoryName)) {
			postEntity.setCategory(categoryEntity);
		}
		
		if (StringUtil.notEmpty(content)) {
			postEntity.setContent(postUpdate.getContent());
		}
		
		if (StringUtil.notEmpty(title)) {
			postEntity.setTitle(postUpdate.getTitle());
		}

		if (uploadFiles != null && uploadFiles.length > 0) {
			files = Arrays.stream(uploadFiles)
					.map(uploadFile -> fileService.upload(postEntity, uploadFile))
					.collect(Collectors.toList());
		}
		
		post = PostMapper.INSTANCE.toDto(postRepository.save(postEntity));
		post.setFiles(files);
		
		return post;
	}
	
	private PostEntity find(Long id) {
		return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
	}
}