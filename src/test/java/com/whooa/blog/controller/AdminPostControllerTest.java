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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.admin.controller.AdminPostController;
import com.whooa.blog.admin.service.AdminPostService;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.SerializeDeserializeUtil;

@WebMvcTest(controllers = {AdminPostController.class})
@ContextConfiguration(classes = {AdminPostController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AllExceptionHandler.class)
public class AdminPostControllerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
	@MockBean
	private AdminPostService adminPostService;
	
	private PostEntity postEntity;
	private CategoryEntity categoryEntity;
	private UserEntity userEntity;
	
	private PostUpdateRequest postUpdate;
	private PostResponse post;
	
	@BeforeAll
	public void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();
				
		categoryEntity = new CategoryEntity().name("카테고리");

		userEntity = new UserEntity()
					.email("user1@user1.com")
					.name("사용자1")
					.password("12345678Aa!@#$%")
					.userRole(UserRole.USER);		
	}
	
	@BeforeEach
	public void setUpEach() {
		String content, title;
		
		content = "포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포스트포";
		title = "포스트";
		
		postEntity = new PostEntity()
				.content(content)
				.title(title)
				.category(categoryEntity)
				.user(userEntity);
		
		postUpdate = new PostUpdateRequest()
				.categoryName(categoryEntity.getName())
				.content("포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포스트2포")
				.title("포스트2");
		
		post = new PostResponse()
				.content(content)
				.title(title);
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeletePost_thenReturnNothing() throws Exception {		
		ResultActions action;
		
		willDoNothing().given(adminPostService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/posts/{id}", postEntity.getId()));

		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeletePost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		
		willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."})).given(adminPostService).delete(any(Long.class));
		
		action = mockMvc.perform(delete("/api/v1/admin/posts/{id}", 100L));
		
		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("필요한 권한이 없어 포스트를 삭제하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeletePost_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		willDoNothing().given(adminPostService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/admin/posts/{id}", postEntity.getId()));

		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("포스트(파일 O)를 수정하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPostWithFiles() throws Exception {
		ResultActions action;
		MockMultipartFile postFile, postUpdateFile;
		
		postFile = new MockMultipartFile("files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes(StandardCharsets.UTF_8));
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willAnswer((answer) -> {
			File file;
			
			file = new File(".txt", MediaType.TEXT_PLAIN_VALUE, postFile.getName(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", postFile.getSize());
			
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());

			post = PostMapper.INSTANCE.toDto(postEntity);
			post.files(List.of(file));
			
			return post;
		});
		
		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.file(postFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.title", is(post.getTitle())))
		.andExpect(jsonPath("$.data.content", is(post.getContent())))
		.andExpect(jsonPath("$.data.files.length()", is(1)));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPostUpdate_whenCallUpdatePost_thenReturnPost() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post = PostMapper.INSTANCE.toDto(postEntity);
			
			return post;
		});
			
		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.title", is(post.getTitle())))
		.andExpect(jsonPath("$.data.content", is(post.getContent())))
		.andExpect(jsonPath("$.data.files.length()", is(0)));
	}
	
	@DisplayName("카테고리 이름이  짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForCategoryName() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;

		postUpdate.categoryName("테");
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post = PostMapper.INSTANCE.toDto(postEntity);
			
			return post;
		});
		
		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("제목이  짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForTitle() throws Exception {		
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdate.title("테");

		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post = PostMapper.INSTANCE.toDto(postEntity);
			
			return post;
		});

		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("내용이  짧아 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPostUpdate_whenCallUpdatePost_thenThrowBadRequestExceptionForContent() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdate.content("실전 내용");
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post = PostMapper.INSTANCE.toDto(postEntity);
			
			return post;
		});
			
		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenPostUpdate_whenCallUpdatePost_thenThrowPostNotFoundException() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));
		
		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willThrow(new PostNotFoundException(Code.NOT_FOUND, new String[] {"포스트가 존재하지 않습니다."}));

		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", 100L)
						.file(postUpdateFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof PostNotFoundException));
	}
	
	@DisplayName("필요한 권한이 없어 포스트를 수정하는데 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenPostUpdate_whenCallUpdatePost_thenThrowUnauthenticatedUserException() throws Exception {
		ResultActions action;
		MockMultipartFile postUpdateFile;
		
		postUpdateFile = new MockMultipartFile("post", null, MediaType.APPLICATION_JSON_VALUE, SerializeDeserializeUtil.serializeToString(postUpdate).getBytes(StandardCharsets.UTF_8));

		given(adminPostService.update(any(Long.class), any(PostUpdateRequest.class), any(MultipartFile[].class))).willAnswer((answer) -> {
			postEntity
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle());
			
			post = PostMapper.INSTANCE.toDto(postEntity);
			
			return post;
		});
					
		action = mockMvc.perform(
						multipart(HttpMethod.PATCH, "/api/v1/admin/posts/{id}", postEntity.getId())
						.file(postUpdateFile)
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.MULTIPART_FORM_DATA)
				);

		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}			
}