package com.whooa.blog.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.PostNotFoundException;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.impl.PostServiceImpl;

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
	}
	
	@DisplayName("포스트 서비스의 create() 메서드로 포스트 생성 성공 테스트")
	@Test
	public void givenPost_whenSavePost_thenReturnSavedPost() {
		// org.mockito.exceptions.misusing.PotentialStubbingProblem 오류.
		// Mockito 2.20부터 lenient() 메서드로 Mockito의 기본 행동인 strict stub을 변경한다.
		// null을 반환해서 any() 사용 -> TODO: 코드 수정
		// BDDMockito 클래스는 메소드를 설정된 응답을 반환하도록 스텁(stub)한다. 
		BDDMockito.lenient().when(postRepository.save(any())).thenReturn(postEntity);
		
		ApiResponse<PostDto.Response> response = postService.create(postDto);
				
		Assertions.assertEquals(response.getMetadata().getCode(), Code.CREATED.getCode());
		Assertions.assertEquals(response.getData().getId(), 1);
	}

	@DisplayName("포스트 서비스의 findOne() 메서드로 포스트 조회 성공 테스트")
	@Test
	public void givenPost_whenFindPostById_thenReturnPost() {
		BDDMockito.lenient().when(postRepository.findById(any())).thenReturn(Optional.of(postEntity));
		
		ApiResponse<PostDto.Response> response = postService.findOne(1L);
				
		Assertions.assertEquals(response.getMetadata().getCode(), Code.OK.getCode());
		Assertions.assertEquals(response.getData().getTitle(), "테스트");
	}
	
	@DisplayName("포스트 서비스의 findOne() 메서드로 포스트 조회 실패 테스트")
	@Test
	public void givenPost_whenFindPostById_thenThrowPostNotFoundException() {
		BDDMockito.lenient().when(postRepository.findById(any())).thenReturn(Optional.empty());
		
		Assertions.assertThrows(PostNotFoundException.class, () -> {
			postService.findOne(1L);
		});
		
		// 예외를 던지고 난 후 제어가 여기로 오지 않을 것이라는 것을 검증한다.
		Mockito.verify(postRepository, never()).save(any(PostEntity.class));
	}	
}