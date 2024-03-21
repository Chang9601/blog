package com.whooa.blog.post.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.PostNotFoundException;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.dto.PostDto.Response;
import com.whooa.blog.post.entity.Post;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.PostService;

@Service
public class PostServiceImpl implements PostService{
	
	private final PostRepository postRepository;
	//private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
	
	// 생성자 주입은 생성자를 사용해서 의존성을 주입한다.
	// Spring 4.3 이전의 경우 @Autowired 어노테이션을 생성자에 추가해야 했지만 이후 버전의 경우 하나의 생성자만 존재하면 이는 선택 사항이다.
	// 즉, 다수의 생성자가 있을 경우 명시적으로 @Autowired 어노테이션을 생성자에 추가해야 한다.
	// 생성자 주입이 세터 주입과 필드 주입보다 권장되는 이유.
	// 1. 모든 필수 의존성이 초기화 시간에 사용 가능하다.
	// 2. 불변성을 보장하고 NullPointerException 예외를 방지한다.
	// 3. 테스트에서 오류를 방지한다.
	public PostServiceImpl(final PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	@Override
	public ApiResponse<PostDto.Response> create(final Post post) {
		Post postEntity = postRepository.save(post);
		
		// 엔티티를 DTO로 변환한다.
		PostDto.Response postDto = PostMapper.INSTANCE.toDto(postEntity);
				
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), postDto, null);
	}

	@Override
	public ApiResponse<List<PostDto.Response>> findAll() {
		List<Post> posts = postRepository.findAll();
		
		List<PostDto.Response> postDtos = posts.stream().map((post) -> PostMapper.INSTANCE.toDto(post)).collect(Collectors.toList());
		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postDtos, null);
	}

	@Override
	public ApiResponse<PostDto.Response> findOne(Long id) {
		String[] details = {"포스트가 존재하지 않습니다."};
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), details));
		
		PostDto.Response postDto = PostMapper.INSTANCE.toDto(post);
		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postDto, null);
	}

	@Override
	public ApiResponse<PostDto.Response> updateOne(Post post, Long id) {
		String[] details = {"포스트가 존재하지 않습니다."};
		Post postEntity = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), details));
		
		postEntity.setTitle(post.getTitle());
		postEntity.setDescription(post.getDescription());
		postEntity.setContent(post.getContent());
		
		Post updatedPost = postRepository.save(postEntity);
		PostDto.Response updatedPostDto = PostMapper.INSTANCE.toDto(updatedPost);
		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), updatedPostDto, null);
	}

	@Override
	public ApiResponse<Response> deleteOne(Long id) {
		String[] failureDetails = {"포스트가 존재하지 않습니다."};
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), failureDetails));
		
		postRepository.delete(post);
		String[] successDetails = {"포스트가 삭제되었습니다."};
		
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, successDetails);
	}
}