package com.whooa.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.utils.PaginationConstants;
import com.whooa.blog.utils.SerializeDeserializeUtil;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

/*
  @WebMvcTest 어노테이션의은 Spring MVC 구성요소에만 중점을 둔 Spring MVC 테스트에 사용할 수있는 주석이다.
  이 주석을 사용하면 완전한 자동 구성이 비활성화되고 MVC 테스트에 관련된 구성만 적용된다.
  예를 들어 @Controller 빈, @ControllerAdvice 빈, @JsonComponent 빈, Converter/GenericConverter 빈, Filter 빈, WebMvcConfigurer 빈 혹은 HandlerMethodArgumentResolver 빈이 포함된다.
  그러나 @Component 빈, @Service 빈 또는 @Repository 빈은 포함되지 않는다.
  전체 애플리케이션 구성을 로드하고 MockMVC를 사용하려는 경우이 @SpringBootTest와 @AutoConfigureMockMvc를 결합한다.
*/
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
	@Autowired
	private MockMvc mockMvc;

	/* @MockBean 어노테이션은 Spring에게 PostService의 모의 인스턴스를 생성하고 애플리케이션 컨텍스트에 추가하여 PostController에 주입되도록 지시한다. */
	@MockBean
	private PostService postService;
	
	@Autowired
	private SerializeDeserializeUtil serializeDeserializeUtil;
	
	private PostDto.CreateRequest createPostDto;
	private PostDto.UpdateRequest updatePostDto;
	private PostDto.Response postDto;
	private PostEntity postEntity;
	private PageDto pageDto;
	private Long eId;
	private Long dneId;

	@BeforeEach
	public void setUp() {
		Long id = 1L;
		String title = "테스트";
		String content = "테스트를 위한 포스트";
		
		
		createPostDto = new PostDto.CreateRequest(title, content);
		updatePostDto = new PostDto.UpdateRequest("실전", "실전을 위한 포스트");
		postDto = new PostDto.Response(id, title, content);
		postEntity = new PostEntity(id, title, content);
		pageDto = new PageDto();
		eId = id;
		dneId = 10000L;
	}
	
	@DisplayName("포스트를 생성하는데 성공한다.")
	@Test
	public void givenCreatePostDto_whenCallCreatePost_thenReturnPostDto() throws Exception {
		given(postService.create(any(PostDto.CreateRequest.class))).willAnswer((invocation) -> {
			PostEntity postEntity = PostMapper.INSTANCE.toEntity(invocation.getArgument(0));

			return PostMapper.INSTANCE.toDto(postEntity);
		});
		
		ResultActions response = mockMvc.perform(post("/api/v1/posts")
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(createPostDto)));
		/* 
		  Hamcrest는 JUnit 및 다른 테스팅 프레임워크와 함께 일반적으로 사용되며 단언문(assertion)을 작성하는 데 사용된다.
		  is() 메서드는 기대되는 값 또는 객체가 실제 값 또는 객체와 동일한지 확인한다.
		*/
		response.andDo(print())
				.andExpect(status().isCreated())
				/*
			      $는 루트를 의미하면 JSON 전체이다.
				  JSON 경로를 분석하여 값을 비교한다.
				*/
				.andExpect(jsonPath("$.data.title", is(postDto.getTitle())))
				.andExpect(jsonPath("$.data.content", is(postDto.getContent())));
	}
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPageDto_whenCallGetPosts_thenReturnPageResponseForAllPostDtos() throws Exception {
		PostEntity postEntity2 = new PostEntity(2L, "실전", "실전을 위한 포스트");
		
		List<PostDto.Response> postDtos = new ArrayList<>();
		
		postDtos.add(PostMapper.INSTANCE.toDto(postEntity));
		postDtos.add(PostMapper.INSTANCE.toDto(postEntity2));
				
		PageResponse<PostDto.Response> pageResponse = PageResponse.handleResponse(postDtos, PaginationConstants.PAGE_SIZE, PaginationConstants.PAGE_NO, postDtos.size(), 1, false, true);

		given(postService.findAll(any(PageDto.class))).willReturn(pageResponse);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("pageNo", String.valueOf(pageDto.getPageNo()));
		params.add("pageSize", String.valueOf(pageDto.getPageSize()));
		params.add("sortBy", pageDto.getSortBy());
		params.add("sortDir", pageDto.getSortDir());
		
		ResultActions response = mockMvc.perform(get("/api/v1/posts").params(params));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(postDtos.size())));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트 목록을 조회하는데 실패한다.")
	@Test
	public void givenPageDto_whenCallGetPosts_thenReturnPageResponseForNothing() throws Exception {
		List<PostDto.Response> postDtos = new ArrayList<>();
				
		PageResponse<PostDto.Response> pageResponse = PageResponse.handleResponse(postDtos, PaginationConstants.PAGE_SIZE, PaginationConstants.PAGE_NO, postDtos.size(), 1, false, true);

		given(postService.findAll(any(PageDto.class))).willReturn(pageResponse);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("pageNo", String.valueOf(pageDto.getPageNo()));
		params.add("pageSize", String.valueOf(pageDto.getPageSize()));
		params.add("sortBy", pageDto.getSortBy());
		params.add("sortDir", pageDto.getSortDir());
				
		ResultActions response = mockMvc.perform(get("/api/v1/posts").params(params));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(postDtos.size())));
	}
	
	@DisplayName("포스트를 조회하는데 성공한다")
	@Test
	public void givenId_whenCallGetPost_thenReturnPostDto() throws Exception {
		given(postService.find(any(Long.class))).willReturn(postDto);
				
		ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", eId));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(postDto.getTitle())))
				.andExpect(jsonPath("$.data.content", is(postDto.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallGetPost_thenThrowPostNotFoundException() throws Exception {				
		given(postService.find(any(Long.class))).willThrow(PostNotFoundException.class);

		ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", dneId));
		
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("포스트를 갱신하는데 성공한다.")
	@Test
	public void givenUpdatePostDto_whenCallUpdatePost_thenReturnPostDto() throws Exception {
		PostEntity updatedPostEntity = new PostEntity(postEntity.getId(), updatePostDto.getTitle(), updatePostDto.getContent());
		PostDto.Response updatedPostDto = new PostDto.Response(updatedPostEntity.getId(), updatedPostEntity.getTitle(), updatedPostEntity.getContent());
		
		given(postService.update(any(PostDto.UpdateRequest.class), any(Long.class))).willReturn(updatedPostDto);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{id}", eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(updatePostDto)));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(updatedPostDto.getTitle())))
				.andExpect(jsonPath("$.data.content", is(updatedPostDto.getContent())));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트를 갱신하는데 실패한다.")
	@Test
	public void givenUpdatePostDto_whenCallUpdatePost_thenThrowPostNotFoundException() throws Exception {				
		given(postService.update(any(PostDto.UpdateRequest.class), any(Long.class))).willThrow(PostNotFoundException.class);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{id}", dneId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(updatePostDto)));
		
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}

	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDeletePost_thenReturnNothing() throws Exception {		
		/* void 반환형을 가진 메서드만 willDoNothing() 메서드를 사용할 수 있다. */
		willDoNothing().given(postService).delete(any(Long.class));

		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", eId));

		response.andDo(print())
				.andExpect(jsonPath("$.metadata.code", is(204)));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeletePost_thenThrowPostNotFoundException() throws Exception {
		willThrow(PostNotFoundException.class).given(postService).delete(any(Long.class));
		
		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", dneId));
				
		response.andDo(print())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
}