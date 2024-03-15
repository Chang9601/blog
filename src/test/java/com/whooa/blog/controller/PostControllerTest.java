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

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.utils.PaginationConstants;
import com.whooa.blog.utils.SerializeDeserializeUtil;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

// @WebMvcTest 어노테이션의은 Spring MVC 구성요소에만 중점을 둔 Spring MVC 테스트에 사용할 수있는 주석이다.
// 이 주석을 사용하면 완전한 자동 구성이 비활성화되고 MVC 테스트에 관련된 구성만 적용된다.
// 예를 들어 @Controller 빈, @ControllerAdvice 빈, @JsonComponent 빈, Converter/GenericConverter 빈, Filter 빈, WebMvcConfigurer 빈 혹은 HandlerMethodArgumentResolver 빈이 포함된다.
// 그러나 @Component 빈, @Service 빈 또는 @Repository 빈은 포함되지 않는다.
// 전체 애플리케이션 구성을 로드하고 MockMVC를 사용하려는 경우이 @SpringBootTest와 @AutoConfigureMockMvc를 결합한다.
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	// @MockBean 어노테이션은 Spring에게 PostService의 모의 인스턴스를 생성하고 애플리케이션 컨텍스트에 추가하여 PostController에 주입되도록 지시한다.
	@MockBean
	private PostService postService;
	
	@Autowired
	private SerializeDeserializeUtil serializeDeserializeUtil;
	
	private PostDto.Request postDto;
	private PostEntity postEntity;
	private PageDto pageDto;

	@BeforeEach
	public void setUp() {
		Long id = 1L;
		String title = "테스트";
		String description = "테스트 포스트";
		String content = "테스트를 위한 포스트";
		
		postDto = new PostDto.Request(title, description, content);
		postEntity = new PostEntity(id, title, description, content);
		pageDto = new PageDto();
	}
	
	@DisplayName("포스트 컨트롤러의 createPost() 메서드로 포스트 생성 성공 테스트")
	@Test
	public void givenPost_whenCreatePost_thenReturnOneCreatedPost() throws Exception {
		given(postService.createOne(any(PostDto.Request.class))).willAnswer((invocation) -> {
			PostEntity postEntity = PostMapper.INSTANCE.toEntity(invocation.getArgument(0));

			return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), PostMapper.INSTANCE.toDto(postEntity), null);
		});
		
		ResultActions response = mockMvc.perform(post("/api/v1/posts")
								.contentType(MediaType.APPLICATION_JSON)
								.content(serializeDeserializeUtil.serialize(postDto)));
				
		// Hamcrest는 JUnit 및 다른 테스팅 프레임워크와 함께 일반적으로 사용되며 단언문(assertion)을 작성하는 데 사용된다.
		// is() 메서드는 기대되는 값 또는 객체가 실제 값 또는 객체와 동일한지 확인한다.
		response.andDo(print())
				.andExpect(status().isCreated())
				// $는 루트를 의미하면 JSON 전체이다.
				// JSON 경로를 분석하여 값을 비교한다.
				.andExpect(jsonPath("$.data.title", is(postEntity.getTitle())))
				.andExpect(jsonPath("$.data.description", is(postEntity.getDescription())));
	}
	
	@DisplayName("포스트 컨트롤러의 getPosts() 메서드로 포스트 목록 조회 성공 테스트")
	@Test
	public void givenPosts_whenGetPosts_thenReturnAllFoundPosts() throws Exception {
		PostEntity anotherPostEntity = new PostEntity(2L, "실전", "실전 포스트", "실전을 위한 포스트");
		
		List<PostDto.Response> postDtos = new ArrayList<>();
		
		postDtos.add(PostMapper.INSTANCE.toDto(postEntity));
		postDtos.add(PostMapper.INSTANCE.toDto(anotherPostEntity));
				
		PageResponse<PostDto.Response> postResponse = PageResponse.handleResponse(postDtos, PaginationConstants.PAGE_SIZE, PaginationConstants.PAGE_NO, postDtos.size(), 1, false, true);

		given(postService.findAll(any(PageDto.class))).willReturn(ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postResponse, null));
		
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
	
	@DisplayName("포스트 컨트롤러의 getPosts() 메서드로 포스트 목록 조회 실패 테스트")
	@Test
	public void givenNoPosts_whenGetPosts_thenReturnNothing() throws Exception {
		List<PostDto.Response> postDtos = new ArrayList<>();
				
		PageResponse<PostDto.Response> postResponse = PageResponse.handleResponse(postDtos, PaginationConstants.PAGE_SIZE, PaginationConstants.PAGE_NO, postDtos.size(), 1, false, true);

		given(postService.findAll(any(PageDto.class))).willReturn(ApiResponse.handleFailure(Code.OK.getCode(), Code.OK.getMessage(), postResponse, null));
		
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
	
	@DisplayName("포스트 컨트롤러의 getPost() 메서드로 포스트 조회 성공 테스트")
	@Test
	public void givenPost_whenGetPost_thenReturnOneFoundPost() throws Exception {
		given(postService.findOne(any(Long.class))).willReturn(ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), PostMapper.INSTANCE.toDto(postEntity), null));
				
		ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", postEntity.getId()));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(postEntity.getTitle())))
				.andExpect(jsonPath("$.data.content", is(postEntity.getContent())));
	}
	
	// TODO: 필터 적용 후 수정이 필요하다.
	@DisplayName("포스트 컨트롤러의 getPost() 메서드로 포스트 조회 실패 테스트")
	@Test
	public void givenPost_whenGetPost_thenThrowPostNotFoundException() throws Exception {				
		given(postService.findOne(any(Long.class))).willReturn(ApiResponse.handleFailure(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), null, null));

		ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", 10000L));
		
		response.andDo(print())
				.andExpect(jsonPath("$.metadata.code", is(404)));
	}

	@DisplayName("포스트 컨트롤러의 updatePost() 메서드로 포스트 갱신 성공 테스트")
	@Test
	public void givenPost_whenUpdatePost_thenReturnOneUpdatedPost() throws Exception {
		PostDto.Request updatePostDto = new PostDto.Request("실전", "실전 포스트", "실전을 위한 포스트");
		PostEntity updatedPostEntity = new PostEntity(postEntity.getId(), "실전", "실전 포스트", "실전을 위한 포스트");
				
		given(postService.updateOne(any(PostDto.Request.class), any(Long.class))).willReturn(ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), PostMapper.INSTANCE.toDto(updatedPostEntity), null));

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{id}", postEntity.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(serializeDeserializeUtil.serialize(updatePostDto)));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(updatedPostEntity.getTitle())))
				.andExpect(jsonPath("$.data.content", is(updatedPostEntity.getContent())));
	}

	@DisplayName("포스트 컨트롤러의 updatePost() 메서드로 포스트 갱신 실패 테스트")
	@Test
	public void givenPost_whenUpdatePost_thenThrowPostNotFoundException() throws Exception {
		PostDto.Request updatePostDto = new PostDto.Request("실전", "실전 포스트", "실전을 위한 포스트");
				
		given(postService.updateOne(any(PostDto.Request.class), any(Long.class))).willReturn(ApiResponse.handleFailure(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), null, null));

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{id}", 10000L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(serializeDeserializeUtil.serialize(updatePostDto)));
		
		response.andDo(print())
		.andExpect(jsonPath("$.metadata.code", is(404)));
	}


	@DisplayName("포스트 컨트롤러의 deletePost() 메서드로 포스트 삭제 성공 테스트")
	@Test
	public void givenPost_whenDeletePost_thenReturnNothing() throws Exception {				
		given(postService.deleteOne(any(Long.class))).willReturn(ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, null));
		
		// void 반환형을 가진 메서드만 willDoNothing() 메서드를 사용할 수 있다.
		// willDoNothing().given(postService).deleteOne(postEntity.getId());

		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", 1L));
				
		response.andDo(print())
		.andExpect(jsonPath("$.metadata.code", is(204)));
	}

	@DisplayName("포스트 컨트롤러의 deletePost() 메서드로 포스트 삭제 실패 테스트")
	@Test
	public void givenPost_whenDeletePost_thenThrowPostNotFoundException() throws Exception {				
		given(postService.deleteOne(any(Long.class))).willReturn((ApiResponse.handleFailure(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), null, null)));
		
		ResultActions response = mockMvc.perform(delete("/api/v1/posts/{id}", 1L));
				
		response.andDo(print())
		.andExpect(jsonPath("$.metadata.code", is(404)));
	}
}