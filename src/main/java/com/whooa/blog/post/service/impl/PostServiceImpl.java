package com.whooa.blog.post.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.elasticsearch.ElasticsearchOperationsUtil;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.doc.PostDoc;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.param.PostSearchParam;
import com.whooa.blog.post.repository.PostJdbcRepository;
import com.whooa.blog.post.repository.PostQueryDslRepository;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.PostElasticsearchService;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.PaginationParam;

@Service
public class PostServiceImpl implements PostService {
	private PostRepository postRepository;
	private PostJdbcRepository postJdbcRepository;
	private PostQueryDslRepository postQueryDslRepository;
	private CategoryRepository categoryRepository;
	private UserRepository userRepository;
	private PostElasticsearchService postElasticsearchService;
	private FileService<PostEntity> fileService;
	private ElasticsearchOperationsUtil<PostDoc> elasticsearchOperationsUtil;

	/*
	 * 생성자 주입은 생성자를 사용해서 의존성을 주입한다.
	 * Spring 4.3 이전의 경우 @Autowired 어노테이션을 생성자에 추가해야 했지만 이후 버전의 경우 하나의 생성자만 존재하면 이는 선택 사항이다.
	 * 즉, 다수의 생성자가 있을 경우 명시적으로 @Autowired 어노테이션을 생성자에 추가해야 한다.
	 * 생성자 주입이 세터 주입과 필드 주입보다 권장되는 이유.
	 * 1. 모든 필수 의존성이 초기화 시간에 사용 가능하다.
	 * 2. 불변성을 보장하고 NullPointerException 예외를 방지한다.
	 * 3. 테스트에서 오류를 방지한다.
	 */
	public PostServiceImpl(
			PostRepository postRepository, 
			PostJdbcRepository postJdbcRepository,
			PostQueryDslRepository postQueryDslRepository,
			CategoryRepository categoryRepository,
			UserRepository userRepository,
			PostElasticsearchService postElasticsearchService,
			FileService<PostEntity> fileService,
			ElasticsearchOperationsUtil<PostDoc> elasticsearchOperationsUtil) {
		this.postRepository = postRepository;
		this.postJdbcRepository = postJdbcRepository;
		this.postQueryDslRepository = postQueryDslRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.postElasticsearchService = postElasticsearchService;
		this.fileService = fileService;
		this.elasticsearchOperationsUtil = elasticsearchOperationsUtil;
	}

	@Transactional
	@Override
	public PostResponse create(PostCreateRequest postCreate, MultipartFile[] uploadFiles, UserDetailsImpl userDetailsImpl) {
		List<File> files = null;
		CategoryEntity categoryEntity;
		PostDoc postDoc;
		PostEntity postEntity, createdPostEntity;
		UserEntity userEntity;
		PostResponse post;
				
		categoryEntity = categoryRepository.findByName(postCreate.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		userEntity = userRepository.findById(userDetailsImpl.getId()).orElseThrow(() -> new UserNotFoundException(Code.NOT_FOUND, new String[] {"아이디에 해당하는 사용자가 존재하지 않습니다."}));
		postEntity = PostMapper.INSTANCE.toEntity(postCreate);

		postEntity.setCategory(categoryEntity);
		postEntity.setUser(userEntity);
		
		createdPostEntity = postRepository.save(postEntity);
		
		if (uploadFiles != null && uploadFiles.length > 0) {
			files = Arrays.stream(uploadFiles)
					.map(uploadFile -> fileService.upload(createdPostEntity, uploadFile))
					.collect(Collectors.toList());
			
			postJdbcRepository.bulkInsert(createdPostEntity.getId(), files);
		}
				
		post = PostMapper.INSTANCE.fromEntity(createdPostEntity);
		post.setFiles(files);

		postDoc = PostMapper.INSTANCE.toDoc(createdPostEntity);
		postDoc.setCategoryName(createdPostEntity.getCategory().getName());
		//postDoc.setCreatedAt(createdPostEntity.getCreatedAt().toLocalDate());
		
		elasticsearchOperationsUtil.create(postDoc);
		
		return post;
	}

	@Transactional
	@Override
	public void delete(Long id, UserDetailsImpl userDetailsImpl) {
		Long userId;
		PostDoc postDoc;
		PostEntity postEntity;
		
		postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		postDoc = elasticsearchOperationsUtil.find(id.toString(), PostDoc.class);
		
		userId = userDetailsImpl.getId();
		
		if (!postEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.FORBIDDEN, new String[] {"로그인한 사용자와 포스트를 생성한 사용자가 일치하지 않습니다."});
		}
		
		postRepository.delete(postEntity);
		elasticsearchOperationsUtil.delete(postDoc);
	}
	
	@Override
	public PostResponse find(Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		return PostMapper.INSTANCE.fromEntity(postEntity);
	}

	@Override
	public PageResponse<PostResponse> findAll(PaginationParam pagination) {
		Page<PostEntity> page;
		Pageable pageable;
		List<PostEntity> postEntities;
		List<PostResponse> postResponse;
		boolean isFirst, isLast;
		int pageNo, pageSize, totalPages;
		long totalElements;
		
		pageable = pagination.makePageable();
		page = postQueryDslRepository.findAll(pageable);
		
		postEntities = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
				
		postResponse = postEntities.stream().map((postEntity) -> PostMapper.INSTANCE.fromEntity(postEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public PageResponse<PostResponse> findAllByCategoryId(Long categoryId, PaginationParam paginationParam) {
		Page<PostEntity> page;
		Pageable pageable;
		List<PostEntity> postEntities;
		List<PostResponse> postResponse;
		boolean isLast, isFirst;
		int pageSize, pageNo, totalPages;
		long totalElements;
		
		pageable = paginationParam.makePageable();
		page = postQueryDslRepository.findAllByCategoryId(categoryId, pageable);
		
		postEntities = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
		
		postResponse = postEntities.stream().map((postEntity) -> PostMapper.INSTANCE.fromEntity(postEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public PageResponse<PostResponse> findAllByDate(Date startDate, Date endDate, PaginationParam paginationParam) {
		Page<PostDoc> page;
		List<PostDoc> postDocs;
		List<PostResponse> postResponse;
		boolean isLast, isFirst;
		int pageSize, pageNo, totalPages;
		long totalElements;
		
		page = postElasticsearchService.findAllByDate(startDate, endDate, paginationParam);
		
		postDocs = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
		
		postResponse = postDocs.stream().map((postDoc) -> PostMapper.INSTANCE.fromDoc(postDoc)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public PageResponse<PostResponse> searchAll(PostSearchParam postSearchParam) {
		Page<PostDoc> page;
		List<PostDoc> postDocs;
		List<PostResponse> postResponse;
		boolean isLast, isFirst;
		int pageSize, pageNo, totalPages;
		long totalElements;
		
		page = postElasticsearchService.searchAll(postSearchParam);

		postDocs = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
		
		postResponse = postDocs.stream().map((postDoc) -> PostMapper.INSTANCE.fromDoc(postDoc)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}
	
	@Override
	public PageResponse<PostResponse> searchAllByDate(PostSearchParam postSearchParam, Date startDate, Date endDate) {
		Page<PostDoc> page;
		List<PostDoc> postDocs;
		List<PostResponse> postResponse;
		boolean isLast, isFirst;
		int pageSize, pageNo, totalPages;
		long totalElements;
		
		page = postElasticsearchService.searchAllByDate(postSearchParam, startDate, endDate);

		postDocs = page.getContent();
		pageSize = page.getSize();
		pageNo = page.getNumber();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		isLast = page.isLast();
		isFirst = page.isFirst();
		
		postResponse = postDocs.stream().map((postDoc) -> PostMapper.INSTANCE.fromDoc(postDoc)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Transactional
	@Override
	public PostResponse update(Long id, PostUpdateRequest postUpdate, MultipartFile[] uploadFiles, UserDetailsImpl userDetailsImpl) {
		CategoryEntity categoryEntity;
		List<File> files = null;
		PostResponse post;
		PostDoc postDoc;
		PostEntity postEntity, updatedPostEntity;
		Long userId;
	
		postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		postDoc = elasticsearchOperationsUtil.find(id.toString(), PostDoc.class);
		categoryEntity = categoryRepository.findByName(postUpdate.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));

		userId = userDetailsImpl.getId();
				
		if (!postEntity.getUser().getId().equals(userId)) {
			throw new UserNotMatchedException(Code.FORBIDDEN, new String[] {"로그인한 사용자와 포스트를 생성한 사용자가 일치하지 않습니다."});
		}
				
		postEntity.setCategory(categoryEntity);
		postEntity.setContent(postUpdate.getContent());
		postEntity.setTitle(postUpdate.getTitle());

		updatedPostEntity = postRepository.save(postEntity);

		if (uploadFiles != null && uploadFiles.length > 0) {
			files = postEntity.getFiles();
			
			files.removeIf((file) -> 1 == 1);
			
			files = Arrays.stream(uploadFiles)
							.map(uploadFile -> fileService.upload(updatedPostEntity, uploadFile))
							.collect(Collectors.toList());
			
			System.out.println("파일@@@@@@@@@@@@@@@@@@@@@2");
			System.out.println(files);
			
			postJdbcRepository.bulkInsert(updatedPostEntity.getId(), files);
		}
		
		postDoc.setCategoryName(updatedPostEntity.getCategory().getName());
		postDoc.setContent(updatedPostEntity.getContent());
		postDoc.setTitle(updatedPostEntity.getTitle());
		
		post = PostMapper.INSTANCE.fromEntity(updatedPostEntity);
		post.setFiles(files);
		
		elasticsearchOperationsUtil.update(postDoc);
		
		return post;
	}
}