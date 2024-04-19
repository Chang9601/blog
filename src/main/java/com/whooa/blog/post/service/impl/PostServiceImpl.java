package com.whooa.blog.post.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.utils.NotNullNotEmptyChecker;

@Service
public class PostServiceImpl implements PostService {
	
	private final PostRepository postRepository;
	//private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
	
	/*
	  생성자 주입은 생성자를 사용해서 의존성을 주입한다.
	  Spring 4.3 이전의 경우 @Autowired 어노테이션을 생성자에 추가해야 했지만 이후 버전의 경우 하나의 생성자만 존재하면 이는 선택 사항이다.
	  즉, 다수의 생성자가 있을 경우 명시적으로 @Autowired 어노테이션을 생성자에 추가해야 한다.
	  생성자 주입이 세터 주입과 필드 주입보다 권장되는 이유.
	  1. 모든 필수 의존성이 초기화 시간에 사용 가능하다.
	  2. 불변성을 보장하고 NullPointerException 예외를 방지한다.
	  3. 테스트에서 오류를 방지한다.
	*/
	public PostServiceImpl(final PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@Override
	public PostDto.Response create(final PostDto.CreateRequest postDto) {				
		return PostMapper.INSTANCE.toDto(postRepository.save(PostMapper.INSTANCE.toEntity(postDto)));
	}

	@Override
	public PageResponse<PostDto.Response> findAll(final PageDto pageDto) {
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
				
		List<PostDto.Response> postResponseDtos = postEntities.stream().map((postEntity) -> PostMapper.INSTANCE.toDto(postEntity)).collect(Collectors.toList());
		
		return PageResponse.handleResponse(postResponseDtos, pageSize, pageNo, totalElements, totalPages, isLast, isFirst);
	}

	@Override
	public PostDto.Response find(final Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[]{"포스트가 존재하지 않습니다."}));
		
		return PostMapper.INSTANCE.toDto(postEntity);
	}

	@Override
	public PostDto.Response update(final PostDto.UpdateRequest postDto, final Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[]{"포스트가 존재하지 않습니다."}));
		
		String title = postDto.getTitle();
		String content = postDto.getContent();
		
		if (NotNullNotEmptyChecker.check(title)) {
			postEntity.setTitle(postDto.getTitle());
		}
				
		if (NotNullNotEmptyChecker.check(content)) {
			postEntity.setContent(postDto.getContent());
		}
				
		return PostMapper.INSTANCE.toDto( postRepository.save(postEntity));
	}

	@Override
	public void delete(final Long id) {
		PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(),  new String[]{"포스트가 존재하지 않습니다."}));
		
		postRepository.delete(postEntity);
	}
}