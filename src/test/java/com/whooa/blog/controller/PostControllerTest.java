package com.whooa.blog.controller;

import java.nio.charset.StandardCharsets;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
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
 * @WebMvcTest 어노테이션의은 Spring MVC 구성요소에만 중점을 둔 Spring MVC 테스트에 사용할 수있는 주석이다.
 * 이 주석을 사용하면 완전한 자동 구성이 비활성화되고 MVC 테스트에 관련된 구성만 적용된다.
 * 예를 들어 @Controller 빈, @ControllerAdvice 빈, @JsonComponent 빈, Converter/GenericConverter 빈, Filter 빈, WebMvcConfigurer 빈 혹은 HandlerMethodArgumentResolver 빈이 포함된다.
 * 그러나 @Component 빈, @Service 빈 또는 @Repository 빈은 포함되지 않는다.
 * 전체 애플리케이션 구성을 로드하고 MockMVC를 사용하려는 경우이 @SpringBootTest와 @AutoConfigureMockMvc를 결합한다.
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
	
	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;
	private PostResponse post;
	private PostEntity postEntity;
	private PageDto page;
	private Long eId;
	private Long dneId;

	@BeforeEach
	public void setUp() {
		Long id = 1L;
		String title = "테스트";
		String content = "테스트를 위한 포스트";
		
		postCreate = new PostCreateRequest(title, content);
		postUpdate = new PostUpdateRequest("실전", "실전을 위한 포스트");
		post = new PostResponse(id, title, content);
		postEntity = new PostEntity(id, title, content);
		page = new PageDto();
		eId = id;
		dneId = 10000L;
	}
	
	@DisplayName("포스트를 생성하는데 성공한다.")
	@Test
	public void givenPostCreate_whenCallCreatePost_thenReturnPost() throws Exception {
		MockMultipartFile file1 = new MockMultipartFile("uploadFiles", "test1.txt", "text/plain", "test1".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile file2 = new MockMultipartFile("uploadFiles", "test2.txt", "text/plain", "test2".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile postCreateFile = new MockMultipartFile("postCreate", null, "application/json", serializeDeserializeUtil.serialize(postCreate).getBytes(StandardCharsets.UTF_8));
		
		// TODO: FileEntity와 FileDto로 변형.
		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class))).willAnswer((invocation) -> {
			PostEntity mockPostEntity = PostMapper.INSTANCE.toEntity(invocation.getArgument(0));
			MockMultipartFile mockFile1 = invocation.getArgument(1);
			MockMultipartFile mockFile2 = invocation.getArgument(2);
			
			return PostMapper.INSTANCE.toDto(postEntity);
		});
		
		ResultActions response = mockMvc.perform(multipart("/api/v1/posts")
										.file(postCreateFile)
										.file(file1)
										.file(file2)
										.contentType(MediaType.MULTIPART_FORM_DATA));
		/* 
		 * Hamcrest는 JUnit 및 다른 테스팅 프레임워크와 함께 일반적으로 사용되며 단언문(assertion)을 작성하는 데 사용된다.
		 * is() 메서드는 기대되는 값 또는 객체가 실제 값 또는 객체와 동일한지 확인한다.
		 */
		response.andDo(print())
				.andExpect(status().isCreated())
				/*
			     * $는 루트를 의미하면 JSON 전체이다.
				 * JSON 경로를 분석하여 값을 비교한다.
				 */
				.andExpect(jsonPath("$.data.title", is(post.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post.getContent())));
	}
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPage_whenCallGetPosts_thenReturnPageResponseForAllPosts() throws Exception {
		PostEntity postEntity2 = new PostEntity(2L, "실전", "실전을 위한 포스트");
		
		List<PostResponse> posts = new ArrayList<>();
		
		posts.add(PostMapper.INSTANCE.toDto(postEntity));
		posts.add(PostMapper.INSTANCE.toDto(postEntity2));
				
		PageResponse<PostResponse> pagePostResponse = PageResponse.handleResponse(posts, PaginationConstants.PAGE_SIZE, PaginationConstants.PAGE_NO, posts.size(), 1, false, true);

		given(postService.findAll(any(PageDto.class))).willReturn(pagePostResponse);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("pageNo", String.valueOf(page.getPageNo()));
		params.add("pageSize", String.valueOf(page.getPageSize()));
		params.add("sortBy", page.getSortBy());
		params.add("sortDir", page.getSortDir());
		
		ResultActions response = mockMvc.perform(get("/api/v1/posts").params(params));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(posts.size())));
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트 목록을 조회하는데 실패한다.")
	@Test
	public void givenPage_whenCallGetPosts_thenReturnPageResponseForNothing() throws Exception {
		List<PostResponse> posts = new ArrayList<>();
				
		PageResponse<PostResponse> pagePostResponse = PageResponse.handleResponse(posts, PaginationConstants.PAGE_SIZE, PaginationConstants.PAGE_NO, posts.size(), 1, false, true);

		given(postService.findAll(any(PageDto.class))).willReturn(pagePostResponse);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("pageNo", String.valueOf(page.getPageNo()));
		params.add("pageSize", String.valueOf(page.getPageSize()));
		params.add("sortBy", page.getSortBy());
		params.add("sortDir", page.getSortDir());
				
		ResultActions response = mockMvc.perform(get("/api/v1/posts").params(params));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(posts.size())));
	}
	
	@DisplayName("포스트를 조회하는데 성공한다")
	@Test
	public void givenId_whenCallGetPost_thenReturnPost() throws Exception {
		given(postService.find(any(Long.class))).willReturn(post);
				
		ResultActions response = mockMvc.perform(get("/api/v1/posts/{id}", eId));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(post.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post.getContent())));
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
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPostDto() throws Exception {
		PostEntity updatedPostEntity = new PostEntity(postEntity.getId(), postUpdate.getTitle(), postUpdate.getContent());
		PostResponse updatedPostDto = new PostResponse(updatedPostEntity.getId(), updatedPostEntity.getTitle(), updatedPostEntity.getContent());
		
		given(postService.update(any(PostUpdateRequest.class), any(Long.class))).willReturn(updatedPostDto);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{id}", eId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(postUpdate)));
		
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(updatedPostDto.getTitle())))
				.andExpect(jsonPath("$.data.content", is(updatedPostDto.getContent())));
	}

	@DisplayName("포스트가 존재하지 않아서 포스트를 갱신하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdatePost_thenThrowPostNotFoundException() throws Exception {				
		given(postService.update(any(PostUpdateRequest.class), any(Long.class))).willThrow(PostNotFoundException.class);

		ResultActions response = mockMvc.perform(put("/api/v1/posts/{id}", dneId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(serializeDeserializeUtil.serialize(postUpdate)));
		
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