package com.whooa.blog.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.impl.PostServiceImpl;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/* Mockito 클래스를 확장해서 의존성을 모의하기 위해 주석을 사용하는 것을 이해한다. */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
	/*
	  @Mock 어노테이션을 사용하여 모의 객체를 생성할 수 있다. 
	  mock() 메서드를 여러 번 호출하는 대신 모의 객체를 여러 곳에서 사용하고자 할 때 유용하다.
	*/
	@Mock
	private PostRepository postRepository;
	
	/*
	  @InjectMocks 어노테이션을 사용하여 모의 객체를 다른 모의 객체에 주입한다. 
	  @InjectMocks 어노테이션은 모의 객체를 생성하고 그 안에 @Mock 어노테이션으로 지정된 모든 모의 객체들을 주입한다. 
	*/
	@InjectMocks
	private PostServiceImpl postService;

	private PostDto.CreateRequest createPostDto;
	private PostDto.UpdateRequest updatePostDto;
	private PostEntity postEntity;
	private PageDto pageDto;
	private Long eId;
	private Long dneId;
	
	/* @BeforeEach 어노테이션이 있는 메서드는 테스트 케이스가 실행되기 전에 실행된다. */
	@BeforeEach
	public void setUp() {
		/*
		  mock() 메서드로 PostRepository 인터페이스의 모의 객체를 생성한다.
	      postRepository = Mockito.mock(PostRepository.class);
		  postService = new PostServiceImpl(postRepository);
		*/
		
		Long id = 1L;
		String title = "테스트";
		String content = "테스트를 위한 포스트";
		
		createPostDto = new PostDto.CreateRequest(title, content);
		updatePostDto = new PostDto.UpdateRequest("실전", "실전을 위한 포스트");
		postEntity = new PostEntity(id, title, content);
		pageDto = new PageDto();
		eId = id;
		dneId = 1000L;
	}
	
	@DisplayName("포스트를 생성하는데 성공한다.")
	@Test
	public void givenCreatePostDto_whenCallCreate_thenReturnPostDto() {
		/*
		  org.mockito.exceptions.misusing.PotentialStubbingProblem 오류.
		  Mockito 2.20부터 lenient() 메서드로 Mockito의 기본 행동인 strict stub을 변경한다.
		  BDDMockito 클래스는 메소드를 설정된 응답을 반환하도록 스텁(stub)한다.
		  BDDMockito.lenient().when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity); 
		*/
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		
		PostDto.Response postDto = postService.create(createPostDto);
		
		assertNotNull(postDto);
		assertEquals(postDto.getId(), postEntity.getId());
		
		then(postRepository).should(times(1)).save(any(PostEntity.class));
	}
	
	@DisplayName("포스트를 작성하지 않아 포스트를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowIllegalArgumentException() {
		given(postRepository.save(any())).willThrow(IllegalArgumentException.class);
		
		assertThrows(IllegalArgumentException.class, () -> {
			postService.create(null);	
		});
		
		then(postRepository).should(times(1)).save(any());
	}	
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPageDto_whenCallFindAll_thenReturnAllPostDtos() {
		PostEntity postEntity2 = new PostEntity(2L, "실전", "실전을 위한 포스트");
		
		Pageable pageable = PageRequest.of(pageDto.getPageNo(), pageDto.getPageSize());
		Page<PostEntity> postEntities = new PageImpl<>(List.of(postEntity, postEntity2), pageable, 2);
		
		given(postRepository.findAll(any(Pageable.class))).willReturn(postEntities);
		
		PageResponse<PostDto.Response> response = postService.findAll(pageDto);
					
		assertNotNull(response.getContent());
		assertEquals(response.getTotalElements(), 2);
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트 목록을 조회하는데 실패한다.")
	@Test
	public void givenPageDto_whenCallFindAll_thenReturnNothing() {
		Pageable pageable = PageRequest.of(pageDto.getPageNo(), pageDto.getPageSize());
		Page<PostEntity> postEntities = new PageImpl<>(List.of(), pageable, 0);
		
		given(postRepository.findAll(any(Pageable.class))).willReturn(postEntities);

		PageResponse<PostDto.Response> response = postService.findAll(pageDto);

		assertEquals(response.getContent(), Collections.emptyList());
		assertEquals(response.getTotalElements(), 0);
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
	}
		
	@DisplayName("포스트를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFind_thenReturnPostDto() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));

		PostDto.Response postDto = postService.find(eId);
		
		assertNotNull(postDto);
		assertEquals(postDto.getId(), postEntity.getId());
		
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFind_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			postService.find(dneId);
		});
	}
	
	@DisplayName("포스트를 갱신하는데 성공한다.")
	@Test
	public void givenUpdatePostDto_whenCallUpdate_thenReturnPostDto() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		
		PostDto.Response postDto = postService.update(updatePostDto, eId);
		
		assertNotNull(postDto);
		assertEquals(postDto.getTitle(), updatePostDto.getTitle());
		assertEquals(postDto.getContent(), updatePostDto.getContent());

		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).save(any(PostEntity.class));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 갱신하는데 실패한다.")
	@Test
	public void givenUpdatePostDto_whenCallUpdate_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			postService.update(updatePostDto, dneId);
		});
		
		/*
		  예외를 던지고 난 후 제어가 여기로 오지 않을 것이라는 것을 검증한다.
		  Mockito.verify(postRepository, never()).save(any(PostEntity.class));
		*/	
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(0)).save(any(PostEntity.class));
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		/* void 반환타입을 가지는 메서드를 스텁할 경우 willDoNothing() 메서드를 사용한다. */
		willDoNothing().given(postRepository).delete(any(PostEntity.class));		
		
		postService.delete(eId);
						
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).delete(any(PostEntity.class));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
				
		assertThrows(PostNotFoundException.class, () -> {
			postService.delete(dneId);
		});
		
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(0)).delete(any(PostEntity.class));
	}		
}