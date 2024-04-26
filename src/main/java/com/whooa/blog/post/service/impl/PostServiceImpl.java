package com.whooa.blog.post.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.file.dto.FileDto;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.utils.NotNullNotEmptyChecker;

@Service
public class PostServiceImpl implements PostService {
	private FileService fileService;
	private PostRepository postRepository;
	// private PostMapper postMapper = Mappers.getMapper(PostMapper.class);
	
	/*
	 * 생성자 주입은 생성자를 사용해서 의존성을 주입한다.
	 * Spring 4.3 이전의 경우 @Autowired 어노테이션을 생성자에 추가해야 했지만 이후 버전의 경우 하나의 생성자만 존재하면 이는 선택 사항이다.
	 * 즉, 다수의 생성자가 있을 경우 명시적으로 @Autowired 어노테이션을 생성자에 추가해야 한다.
	 * 생성자 주입이 세터 주입과 필드 주입보다 권장되는 이유.
	 * 1. 모든 필수 의존성이 초기화 시간에 사용 가능하다.
	 * 2. 불변성을 보장하고 NullPointerException 예외를 방지한다.
	 * 3. 테스트에서 오류를 방지한다.
	 */
	public PostServiceImpl(FileService fileService, PostRepository postRepository) {
		this.fileService = fileService;
		this.postRepository = postRepository;
	}

	@Override
	public PostResponse create(PostCreateRequest postCreate, MultipartFile[] uploadFiles) {
		  PostEntity postEntity = postRepository.save(PostMapper.INSTANCE.toEntity(postCreate));
		  PostResponse post = PostMapper.INSTANCE.toDto(postEntity);
		  
		  if (uploadFiles != null && uploadFiles.length > 0) {
		    List<FileDto> files = Arrays.stream(uploadFiles)
		                  .map(uploadFile -> fileService.upload(uploadFile, postEntity))
		                  .collect(Collectors.toList());
		    post.setFiles(files);
		    post = PostMapper.INSTANCE.toDto(postRepository.save(postEntity));
		  }
		  
		  return post;
	}

	@Override
	public PageResponse<PostResponse> findAll(PageDto pageDto) {
		// TODO: 페이지 관련 코드 간소화.
		String sortBy = pageDto.getSortBy();		
		Sort sort = pageDto.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(pageDto.getPageNo(), pageDto.getPageSize(), sort);
		
		Page<PostEntity> posts = postRepository.findAll(pageable);
		
		List<PostEntity> postEntities = posts.getContent();
		int pageSize = posts.getSize();
		int pageNo = posts.getNumber();
		long totalElements = posts.getTotalElements();
		int totalPages = posts.getTotalPages();
		boolean isLast = posts.isLast();
		boolean isFirst = posts.isFirst();
				
		List<PostResponse> postResponse = postEntities.stream().map((postEntity) -> PostMapper.INSTANCE.toDto(postEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponse, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public PostResponse find(Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		return PostMapper.INSTANCE.toDto(postEntity);
	}

	@Override
	public PostResponse update(PostUpdateRequest postDto, Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		String title = postDto.getTitle();
		String content = postDto.getContent();
		
		if (NotNullNotEmptyChecker.check(title)) {
			postEntity.setTitle(postDto.getTitle());
		}
				
		if (NotNullNotEmptyChecker.check(content)) {
			postEntity.setContent(postDto.getContent());
		}
				
		return PostMapper.INSTANCE.toDto(postRepository.save(postEntity));
	}

	@Override
	public void delete(Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));
		
		postRepository.delete(postEntity);
	}
}