package com.whooa.blog.post.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.NotNullNotEmptyChecker;
import com.whooa.blog.util.PaginationUtil;

@Service
public class PostServiceImpl implements PostService {
	private CategoryRepository categoryRepository;
	private PostRepository postRepository;
	private UserRepository userRepository;
	private FileService fileService;
	
	/*
	 * 생성자 주입은 생성자를 사용해서 의존성을 주입한다.
	 * Spring 4.3 이전의 경우 @Autowired 어노테이션을 생성자에 추가해야 했지만 이후 버전의 경우 하나의 생성자만 존재하면 이는 선택 사항이다.
	 * 즉, 다수의 생성자가 있을 경우 명시적으로 @Autowired 어노테이션을 생성자에 추가해야 한다.
	 * 생성자 주입이 세터 주입과 필드 주입보다 권장되는 이유.
	 * 1. 모든 필수 의존성이 초기화 시간에 사용 가능하다.
	 * 2. 불변성을 보장하고 NullPointerException 예외를 방지한다.
	 * 3. 테스트에서 오류를 방지한다.
	 */
	public PostServiceImpl(CategoryRepository categoryRepository, PostRepository postRepository, UserRepository userRepository, FileService fileService) {
		this.categoryRepository = categoryRepository;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.fileService = fileService;
	}

	@Override
	public PostResponse create(PostCreateRequest postCreate, MultipartFile[] uploadFiles, UserDetailsImpl userDetailsImpl) {
		List<File> files = null;
				
		String title = postCreate.getTitle();
		String content = postCreate.getContent();
		String categoryName = postCreate.getCategoryName();
		
		CategoryEntity categoryEntity = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		UserEntity userEntity = userRepository.findById(userDetailsImpl.getId()).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		PostEntity postEntity = new PostEntity()
				.content(content)
				.title(title)
				.category(categoryEntity)
				.user(userEntity);
	
		// TODO: Mapper에서 연관관계 정리.
		// PostEntity postEntity = postRepository.save(PostMapper.INSTANCE.toEntity(postCreate));
						
		if (uploadFiles != null && uploadFiles.length > 0) {
			files = Arrays.stream(uploadFiles)
					.map(uploadFile -> fileService.upload(postEntity, uploadFile))
					.collect(Collectors.toList());
		}
	
		PostResponse post = PostMapper.INSTANCE.toDto(postRepository.save(postEntity));
		post.setFiles(files);

		return post;
	}

	@Override
	public void delete(Long id, UserDetailsImpl userDetailsImpl) {		
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		UserEntity userEntity = userRepository.findById(userDetailsImpl.getId()).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		
		Long userId = userEntity.getId();
		
		if (!postEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 포스트를 생성한 사용자가 일치하지 않습니다."});
		}
		
		postRepository.delete(postEntity);
	}
	
	@Override
	public PostResponse find(Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		return PostMapper.INSTANCE.toDto(postEntity);
	}

	@Override
	public PageResponse<PostResponse> findAll(PaginationUtil pagination) {
		Pageable pageable = pagination.makePageable();
		
		Page<PostEntity> page = postRepository.findAll(pageable);
		
		List<PostEntity> postEntities = page.getContent();
		int pageSize = page.getSize();
		int pageNo = page.getNumber();
		long totalElements = page.getTotalElements();
		int totalPages = page.getTotalPages();
		boolean isLast = page.isLast();
		boolean isFirst = page.isFirst();
				
		List<PostResponse> postResponse = postEntities.stream().map((postEntity) -> PostMapper.INSTANCE.toDto(postEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public PageResponse<PostResponse> findAllByCategoryId(Long categoryId, PaginationUtil pagination) {
		//categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		Pageable pageable = pagination.makePageable();
		
		Page<PostEntity> page = postRepository.findByCategoryId(categoryId, pageable);
		
		List<PostEntity> postEntities = page.getContent();
		int pageSize = page.getSize();
		int pageNo = page.getNumber();
		long totalElements = page.getTotalElements();
		int totalPages = page.getTotalPages();
		boolean isLast = page.isLast();
		boolean isFirst = page.isFirst();
		
		List<PostResponse> postResponse = postEntities.stream().map((postEntity) -> PostMapper.INSTANCE.toDto(postEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public PostResponse update(Long id, PostUpdateRequest postUpdate, MultipartFile[] uploadFiles, UserDetailsImpl userDetailsImpl) {
		List<File> files = null;

		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		CategoryEntity categoryEntity = categoryRepository.findByName(postUpdate.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		UserEntity userEntity = userRepository.findById(userDetailsImpl.getId()).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));

		Long userId = userEntity.getId();
				
		if (!postEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.USER_NOT_MATCHED, new String[] {"로그인한 사용자와 포스트를 생성한 사용자가 일치하지 않습니다."});
		}
		
		if (NotNullNotEmptyChecker.check(postUpdate.getTitle())) {
			postEntity.title(postUpdate.getTitle());
		}
				
		if (NotNullNotEmptyChecker.check(postUpdate.getContent())) {
			postEntity.content(postUpdate.getContent());
		}
		
		if (NotNullNotEmptyChecker.check(postUpdate.getCategoryName())) {
			postEntity.category(categoryEntity);
		}
		
		if (uploadFiles != null && uploadFiles.length > 0) {
			files = Arrays.stream(uploadFiles)
					.map(uploadFile -> fileService.upload(postEntity, uploadFile))
					.collect(Collectors.toList());
		}
		
		PostResponse post = PostMapper.INSTANCE.toDto(postRepository.save(postEntity));
		post.setFiles(files);
		
		return post;
	}
}