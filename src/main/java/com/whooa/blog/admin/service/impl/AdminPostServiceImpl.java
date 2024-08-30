package com.whooa.blog.admin.service.impl;

import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.whooa.blog.post.repository.PostJdbcRepository;
import com.whooa.blog.post.repository.PostRepository;

@Service
public class AdminPostServiceImpl implements AdminPostService {
	private PostRepository postRepository;
	private PostJdbcRepository postJdbcRepository;
	private CategoryRepository categoryRepository;
	private FileService<PostEntity> fileService;

	public AdminPostServiceImpl(
			PostRepository postRepository,
			PostJdbcRepository postJdbcRepository,
			CategoryRepository categoryRepository,
			FileService<PostEntity> fileService) {
		this.postRepository = postRepository;
		this.postJdbcRepository = postJdbcRepository;
		this.categoryRepository = categoryRepository;
		this.fileService = fileService;
	}

	@Override
	public void delete(Long id) {
		PostEntity postEntity;
		
		postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
				
		postRepository.delete(postEntity);
	}

	@Transactional
	@Override
	public PostResponse update(Long id, PostUpdateRequest postUpdate, MultipartFile[] uploadFiles) {
		CategoryEntity categoryEntity;
		List<File> files = null;
		PostResponse post;
		PostEntity postEntity;
		
		postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));	
		categoryEntity = categoryRepository.findByName(postUpdate.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		postEntity.setCategory(categoryEntity);
		postEntity.setContent(postUpdate.getContent());
		postEntity.setTitle(postUpdate.getTitle());
		
		if (uploadFiles != null && uploadFiles.length > 0) {
			files = Arrays.stream(uploadFiles)
					.map(uploadFile -> fileService.upload(postEntity, uploadFile))
					.collect(Collectors.toList());
		}
		
		if (files != null && files.size() > 0) {
			postJdbcRepository.bulkInsert(postEntity.getId(), files);
		}
		
		post = PostMapper.INSTANCE.fromEntity(postRepository.save(postEntity));
		post.setFiles(files);
		
		return post;
	}
}