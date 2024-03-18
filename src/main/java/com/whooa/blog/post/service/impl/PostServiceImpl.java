package com.whooa.blog.post.service.impl;

import org.springframework.stereotype.Service;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.PostService;

@Service
public class PostServiceImpl implements PostService{
	
	private final PostRepository postRepository;
	
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
	public ApiResponse<PostDto.Response> createPost(final PostDto.Request postDto) {
		// DTO를 엔티티로 변환한다.
		
		return null;
	}
}