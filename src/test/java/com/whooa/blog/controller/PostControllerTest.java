package com.whooa.blog.controller;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.controller.PostController;
import com.whooa.blog.post.dto.PostDTO.PostCreateRequest;
import com.whooa.blog.post.dto.PostDTO.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDTO.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.service.PostService;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

/*
 * @WebMvcTest 어노테이션은 Spring MVC 구성요소에만 중점을 둔 Spring MVC 테스트에 사용할 수있는 주석이다.
 * 이 주석을 사용하면 완전한 자동 구성이 비활성화되고 MVC 테스트에 관련된 구성만 적용된다.
 * 예를 들어 @Controller 빈, @ControllerAdvice 빈, @JsonComponent 빈, Converter/GenericConverter 빈, Filter 빈, WebMvcConfigurer 빈 혹은 HandlerMethodArgumentResolver 빈이 포함된다.
 * 그러나 @Component 빈, @Service 빈 또는 @Repository 빈은 포함되지 않는다.
 * 전체 애플리케이션 구성을 로드하고 MockMVC를 사용하려는 경우 @SpringBootTest 어노테이션과 @AutoConfigureMockMvc 어노테이션을 결합한다.
 */
@WebMvcTest(controllers = {PostController.class})
@ContextConfiguration(classes = {PostController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/* 
 * 컨트롤러를 @WebMvcTest 어노테이션과 사용할 때, Spring 설정에서 컨트롤러 어드바이스가 사용되지 않는다.
 * 따라서, @Import 어노테이션을 통해 Spring에게 명시적으로 컨트롤러 어드바이스를 사용하라고 지시한다.
 */
@Import(AllExceptionHandler.class)
public class PostControllerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
	/* @MockBean 어노테이션은 Spring에게 PostService의 모의 인스턴스를 생성하고 애플리케이션 컨텍스트에 추가하여 PostController에 주입되도록 지시한다. */
	@MockBean
	private PostService postService;
	
	private PostEntity postEntity;
	private CategoryEntity categoryEntity;
	private UserEntity userEntity;

	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;
	private PostResponse post1;
	
	private PaginationUtil pagination;

	@BeforeAll
	public void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();
				
		categoryEntity = new CategoryEntity().name("테스트 카테고리");

		userEntity = new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER);
		
		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	public void setUpEach() {
		String content = "테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용테스트 내용";
		String title = "테스트 제목";
		
		postEntity = new PostEntity()
				.content(content)
				.title(title)
				.category(categoryEntity)
				.user(userEntity);
				
		postCreate = new PostCreateRequest()
				.categoryName(categoryEntity.getName())
				.content(content)
				.title(title);
		
		postUpdate = new PostUpdateRequest()
				.categoryName(categoryEntity.getName())
				.content("실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용실전 내용")
				.title("실전 제목");
		
		post1 = new PostResponse()
				.content(content)
				.title(title);
	}
	
	@DisplayName("포스트(파일 X)를 생성하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenPostCreate_whenCallCreatePost_thenReturnPost() throws Exception {
		MockMultipartFile postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> { 
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
					
		ResultActions action = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/posts")
										.file(postCreateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));
				
		/* 
		 * Hamcrest는 JUnit 및 다른 테스팅 프레임워크와 함께 일반적으로 사용되며 단언문(assertion)을 작성하는 데 사용된다.
		 * is() 메서드는 기대되는 값 또는 객체가 실제 값 또는 객체와 동일한지 확인한다.
		 */
		action.andDo(print())
				.andExpect(status().isCreated())
				/*
			     * $는 루트를 의미하면 JSON 전체이다.
				 * JSON 경로를 분석하여 값을 비교한다.
				 */
				.andExpect(jsonPath("$.data.title", is(post1.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post1.getContent())));
	}
	
	@DisplayName("포스트(파일 O)를 생성하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenPostCreate_whenCallCreatePost_thenReturnPostWithFiles() throws Exception {
		MockMultipartFile postFile1 = new MockMultipartFile("files", "test1.txt", MediaType.TEXT_PLAIN_VALUE, "test1".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile postFile2 = new MockMultipartFile("files", "test2.txt", MediaType.TEXT_PLAIN_VALUE, "test2".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			File file1 = new File(".txt", MediaType.TEXT_PLAIN_VALUE, postFile1.getName(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", postFile1.getSize());
			File file2 = new File(".txt", MediaType.TEXT_PLAIN_VALUE, postFile2.getName(), "D:\\spring-workspace\\whooa-blog\\upload\\test2.txt", postFile2.getSize());
					
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			post1.files(List.of(file1, file2));
			
			return post1;
		});
					
		ResultActions action = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/posts")
										.file(postCreateFile)
										.file(postFile1)
										.file(postFile2)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));
		action.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.title", is(post1.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post1.getContent())))
				.andExpect(jsonPath("$.data.files.length()", is(2)));
	}

	@DisplayName("카테고리 이름이 너무 짧아 포스트를 생성하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostCreate_whenCallCreatePost_thenThrowBadRequestExceptionForCategoryName() throws Exception {
		postCreate.categoryName("테");
		MockMultipartFile postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> { 
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
					
		ResultActions action = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/posts")
										.file(postCreateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("제목이 너무 짧아 포스트를 생성하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostCreate_whenCallCreatePost_thenThrowBadRequestExceptionForTitle() throws Exception {
		postCreate.title("테");
		MockMultipartFile postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> { 
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
					
		ResultActions action = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/posts")
										.file(postCreateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@DisplayName("내용이 너무 짧아 포스트를 생성하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostCreate_whenCallCreatePost_thenThrowBadRequestExceptionForContent() throws Exception {
		postCreate.content("테스트 내용");
		MockMultipartFile postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> { 
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
					
		ResultActions action = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/posts")
										.file(postCreateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}	
		
	@DisplayName("인증되어 있지 않아 포스트를 생성하는데 실패한다.")
	@Test
	public void givenPostCreate_whenCallCreatePost_thenThrowUnauthenticatedUserException() throws Exception {
		MockMultipartFile postCreateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postCreate).getBytes(StandardCharsets.UTF_8));

		given(postService.create(any(PostCreateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> { 
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});

		ResultActions action = mockMvc.perform(multipart("/api/v1/posts")
										.file(postCreateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));
		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeletePost_thenReturnNothing() throws Exception {		
		/* void 반환형을 가진 메서드만 willDoNothing() 메서드를 사용할 수 있다. */
		willDoNothing().given(postService).delete(any(Long.class), any(UserDetailsImpl.class));

		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{id}", postEntity.getId()));

		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeletePost_thenThrowPostNotFoundException() throws Exception {
		willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."})).given(postService).delete(any(Long.class), any(UserDetailsImpl.class));
		
		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{id}", 100L));
		
		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("인증되어 있지 않아 포스트를 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDeletePost_thenThrowUnauthenticatedUserException() throws Exception {		
		willDoNothing().given(postService).delete(any(Long.class), any(UserDetailsImpl.class));

		ResultActions action = mockMvc.perform(delete("/api/v1/posts/{id}", postEntity.getId()));

		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}	

	@DisplayName("포스트를 조회하는데 성공한다")
	@Test
	public void givenId_whenCallGetPost_thenReturnPost() throws Exception {
		given(postService.find(any(Long.class))).willReturn(post1);
				
		ResultActions action = mockMvc.perform(get("/api/v1/posts/{id}", postEntity.getId())
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(post1.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post1.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallGetPost_thenThrowPostNotFoundException() throws Exception {
		given(postService.find(any(Long.class))).willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));

		ResultActions action = mockMvc.perform(get("/api/v1/posts/{id}", 100L)
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}	
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallGetPosts_thenReturnPosts() throws Exception {
		PostResponse post2 = new PostResponse()
				.content("실전 내용")
				.title("실전 제목");

		PageResponse<PostResponse> page = PageResponse.handleResponse(List.of(post1, post2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, false, true);

		given(postService.findAll(any(PaginationUtil.class))).willReturn(page);
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		ResultActions action = mockMvc.perform(get("/api/v1/posts")
										.params(params)
										.characterEncoding(StandardCharsets.UTF_8));
		
		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.size()", is(page.getContent().size())));
	}

	@DisplayName("카테고리 이름이 너무 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForCategoryName() throws Exception {
		postUpdate.categoryName("테");
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
			
		System.out.println("COSMONAUT2" + post1);

		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", postEntity.getId())
										.file(postUpdateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("제목이 너무 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForTitle() throws Exception {
		postUpdate.title("테");
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});

		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", postEntity.getId())
										.file(postUpdateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("내용이 너무 짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForContent() throws Exception {
		postUpdate.content("실전 내용");
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
			
		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", postEntity.getId())
										.file(postUpdateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@DisplayName("포스트(파일 O)를 수정하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPostWithFiles() throws Exception {
		MockMultipartFile postFile = new MockMultipartFile("files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			File file = new File(".txt", MediaType.TEXT_PLAIN_VALUE, postFile.getName(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", postFile.getSize());
			
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());

			post1 = PostMapper.INSTANCE.toDto(postEntity);
			post1.files(List.of(file));
			
			return post1;
		});
		
		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", postEntity.getId())
										.file(postUpdateFile)
										.file(postFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(post1.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post1.getContent())))
				.andExpect(jsonPath("$.data.files.length()", is(1)));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPost() throws Exception {
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
			
		System.out.println("COSMONAUT2" + post1);

		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", postEntity.getId())
										.file(postUpdateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.title", is(post1.getTitle())))
				.andExpect(jsonPath("$.data.content", is(post1.getContent())));
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenThrowPostNotFoundException() throws Exception {				
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));

		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", 100L)
										.file(postUpdateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("인증되어 있지 않아 포스트를 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdatePost_thenThrowUnauthenticatedUserException() throws Exception {
		MockMultipartFile postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		given(postService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class), any(UserDetailsImpl.class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post1 = PostMapper.INSTANCE.toDto(postEntity);
			
			return post1;
		});
					
		ResultActions action = mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/posts/{id}", postEntity.getId())
										.file(postUpdateFile)
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.MULTIPART_FORM_DATA));

		action.andDo(print())
				.andExpect(status().isUnauthorized());
	}		
}