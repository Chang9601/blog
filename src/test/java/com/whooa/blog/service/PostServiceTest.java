package com.whooa.blog.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.common.exception.PostNotFoundException;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.impl.PostServiceImpl;

import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;

// Mockito 클래스를 확장해서 의존성을 모의하기 위해 주석을 사용하는 것을 이해한다.
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

	// @Mock 어노테이션을 사용하여 모의 객체를 생성할 수 있다. 
	// mock() 메서드를 여러 번 호출하는 대신 모의 객체를 여러 곳에서 사용하고자 할 때 유용하다.
	@Mock
	private PostRepository postRepository;
	
	// @InjectMocks 어노테이션을 사용하여 모의 객체를 다른 모의 객체에 주입한다. 
	// @InjectMocks 어노테이션은 모의 객체를 생성하고 그 안에 @Mock 어노테이션으로 지정된 모든 모의 객체들을 주입한다.
	@InjectMocks
	private PostServiceImpl postService;
	
	private PostDto.Request postDto;
	private PostEntity postEntity;
	private PageDto pageDto;
	
	// @BeforeEach 어노테이션이 있는 메서드는 테스트 케이스가 실행되기 전에 실행된다.
	@BeforeEach
	public void setUp() {
		// mock() 메서드로 PostRepository 인터페이스의 모의 객체를 생성한다.
		// postRepository = Mockito.mock(PostRepository.class);
		// postService = new PostServiceImpl(postRepository);
		
		Long id = 1L;
		String title = "테스트";
		String description = "테스트 포스트";
		String content = "테스트를 위한 포스트";
		
		postDto = new PostDto.Request(title, description, content);
		postEntity = new PostEntity(id, title, description, content);
		pageDto = new PageDto();
	}
	
	@DisplayName("포스트 서비스의 createOne() 메서드로 포스트 생성 성공 테스트")
	@Test
	public void givenPost_whenCreateOne_thenReturnCreatedPost() {
		// org.mockito.exceptions.misusing.PotentialStubbingProblem 오류.
		// Mockito 2.20부터 lenient() 메서드로 Mockito의 기본 행동인 strict stub을 변경한다.
		// BDDMockito 클래스는 메소드를 설정된 응답을 반환하도록 스텁(stub)한다.
		// BDDMockito.lenient().when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		
		ApiResponse<PostDto.Response> response = postService.createOne(postDto);
				
		Assertions.assertEquals(response.getMetadata().getCode(), Code.CREATED.getCode());
		Assertions.assertEquals(response.getData().getId(), 1);
		
		then(postRepository).should(times(1)).save(any(PostEntity.class));
	}
	
	@DisplayName("포스트 서비스의 createOne() 메서드로 포스트 생성 실패 테스트")
	@Test
	public void givenPost_whenCreateOne_thenThrowIllegalArgumentException() {
		given(postRepository.save(any())).willThrow(IllegalArgumentException.class);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			postService.createOne(null);	
		});
		
		then(postRepository).should(times(1)).save(any());
	}	
	
	@DisplayName("포스트 서비스의 findAll() 메서드로 포스트 목록 조회 성공 테스트")
	@Test
	public void givenPosts_whenFindAll_thenReturnAllFoundPosts() {
		PostEntity anotherPostEntity = new PostEntity(2L, "실전", "실전 포스트", "실전을 위한 포스트");
		
		Pageable pageable = PageRequest.of(pageDto.getPageNo(), pageDto.getPageSize());
		Page<PostEntity> posts = new PageImpl<>(List.of(postEntity, anotherPostEntity), pageable, 0);
		
		given(postRepository.findAll(any(Pageable.class))).willReturn(posts);
		
		ApiResponse<PageResponse<PostDto.Response>> response = postService.findAll(pageDto);
						
		Assertions.assertEquals(response.getMetadata().getCode(), Code.OK.getCode());
		Assertions.assertNotNull(response.getData().getContent());
		Assertions.assertEquals(response.getData().getTotalElements(), 2);
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
	}

	@DisplayName("포스트 서비스의 findAll() 메서드로 포스트 목록 조회 실패 테스트")
	@Test
	public void givenNoPosts_whenFindAll_thenReturnNothing() {		
		Pageable pageable = PageRequest.of(pageDto.getPageNo(), pageDto.getPageSize());
		Page<PostEntity> posts = new PageImpl<>(List.of(), pageable, 0);
		
		given(postRepository.findAll(any(Pageable.class))).willReturn(posts);

		ApiResponse<PageResponse<PostDto.Response>> response = postService.findAll(pageDto);
								
		Assertions.assertEquals(response.getData().getContent(), Collections.emptyList());
		Assertions.assertEquals(response.getData().getTotalElements(), 0);
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
	}
		
	@DisplayName("포스트 서비스의 findOne() 메서드로 포스트 조회 성공 테스트")
	@Test
	public void givenPost_whenFindOne_thenReturnOneFoundPost() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		ApiResponse<PostDto.Response> response = postService.findOne(1L);
				
		Assertions.assertEquals(response.getMetadata().getCode(), Code.OK.getCode());
		Assertions.assertEquals(response.getData().getTitle(), "테스트");
		
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트 서비스의 findOne() 메서드로 포스트 조회 실패 테스트")
	@Test
	public void givenPost_whenFindOne_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		Assertions.assertThrows(PostNotFoundException.class, () -> {
			postService.findOne(1L);
		});
	}
	
	@DisplayName("포스트 서비스의 updateOne() 메서드로 포스트 갱신 성공 테스트")
	@Test
	public void givenPost_whenUpdateOne_thenReturnOneUpdatedPost() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(postRepository.save(postEntity)).willReturn(postEntity);
		
		postDto.setTitle("실전");
		postDto.setDescription("실전 포스트");
		
		ApiResponse<PostDto.Response> response = postService.updateOne(postDto, 1L);
				
		Assertions.assertEquals(response.getMetadata().getCode(), Code.OK.getCode());
		Assertions.assertEquals(response.getData().getTitle(), "실전");
		Assertions.assertEquals(response.getData().getDescription(), "실전 포스트");
		
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).save(any(PostEntity.class));
	}
	
	@DisplayName("포스트 서비스의 updateOne() 메서드로 포스트 갱신 실패 테스트")
	@Test
	public void givenPost_whenUpdateOne_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		postDto.setTitle("실전");
		postDto.setDescription("실전 포스트");
				
		Assertions.assertThrows(PostNotFoundException.class, () -> {
			postService.updateOne(postDto, 1L);
		});

		// 예외를 던지고 난 후 제어가 여기로 오지 않을 것이라는 것을 검증한다.
		// Mockito.verify(postRepository, never()).save(any(PostEntity.class));
		
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(0)).save(any(PostEntity.class));
	}
	
	@DisplayName("포스트 서비스의 deleteOne() 메서드로 포스트 삭제 성공 테스트")
	@Test
	public void givenPost_whenDeleteOne_thenReturnNothing() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		// void 반환타입을 가지는 메서드를 스텁할 경우 willDoNothing() 메서드를 사용한다.
		willDoNothing().given(postRepository).delete(postEntity);		
		
		ApiResponse<PostDto.Response> response = postService.deleteOne(1L);
				
		Assertions.assertEquals(response.getMetadata().getCode(), Code.NO_CONTENT.getCode());
		
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).delete(any(PostEntity.class));
	}
	
	@DisplayName("포스트 서비스의 deleteOne() 메서드로 포스트 삭제 실패 테스트")
	@Test
	public void givenPost_whenDeleteOne_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
				
		Assertions.assertThrows(PostNotFoundException.class, () -> {
			postService.deleteOne(1L);
		});
		
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(0)).delete(any(PostEntity.class));
	}		
}