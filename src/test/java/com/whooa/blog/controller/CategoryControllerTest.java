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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.whooa.blog.category.controller.CategoryController;
import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDto.CategoryUpdateRequest;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.exception.DuplicateCategoryException;
import com.whooa.blog.category.mapper.CategoryMapper;
import com.whooa.blog.category.service.CategoryService;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AllExceptionHandler;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

@WebMvcTest(controllers = {CategoryController.class})
@ContextConfiguration(classes = {CategoryController.class, TestSecurityConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AllExceptionHandler.class)
public class CategoryControllerTest {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
 
	@MockBean
	private CategoryService categoryService;
	
	private CategoryEntity categoryEntity;
	
	private CategoryCreateRequest categoryCreate;
	private CategoryUpdateRequest categoryUpdate;
	private CategoryResponse category1;

	private PaginationUtil pagination;
	
	@BeforeAll
	public void setUpAll() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter("utf-8", true))
				.apply(springSecurity()).build();
				
		pagination = new PaginationUtil();
	}
	
	@BeforeEach
	public void setUpEach() {
		String name = "카테고리1";
		
		categoryEntity = new CategoryEntity().name(name);

		categoryCreate = new CategoryCreateRequest().name(name);
		categoryUpdate = new CategoryUpdateRequest().name("카테고리2");
		
		category1 = new CategoryResponse().name(name);
	}
	
	@DisplayName("카테고리를 생성하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCategoryCreate_whenCallCreateCategory_thenReturnCategory() throws Exception {
		ResultActions action;
		
		given(categoryService.create(any(CategoryCreateRequest.class))).willAnswer((answer) -> {
			category1 = CategoryMapper.INSTANCE.toDto(categoryEntity);
			
			return category1;
		});

		action = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);

		action
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.data.name", is(category1.getName())));
	}
	
	@DisplayName("이름이  짧아 카테고리를 생성하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCategoryCreate_whenCallCreateCategory_thenThrowBadRqeustExceptionForName() throws Exception {
		ResultActions action;
		
		given(categoryService.create(any(CategoryCreateRequest.class))).willAnswer((answer) -> {
			category1 = CategoryMapper.INSTANCE.toDto(categoryEntity);
			
			return category1;
		});

		categoryCreate.name("가");
		action = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);

		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("카테고리가 이미 존재하여 생성하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCategoryCreate_whenCallCreateCategory_thenThrowDuplicateCategoryException() throws Exception {
		ResultActions action;
		
		given(categoryService.create(any(CategoryCreateRequest.class))).willThrow(new DuplicateCategoryException(Code.CONFLICT, new String[] {"카테고리가 존재합니다."}));

		action = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON));
		
		action
		.andDo(print())
		.andExpect(status().isConflict())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof DuplicateCategoryException));
	}

	@DisplayName("카테고리를 생성하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCategoryCreate_whenCallCreateCategory_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		given(categoryService.create(any(CategoryCreateRequest.class))).willAnswer((answer) -> {
			category1 = CategoryMapper.INSTANCE.toDto(categoryEntity);
			
			return category1;
		});

		action = mockMvc.perform(
						post("/api/v1/categories")
						.content(SerializeDeserializeUtil.serializeToString(categoryCreate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);

		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("카테고리를 삭제하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteCategory_thenReturnNothing() throws Exception {
		ResultActions action;
		
		willDoNothing().given(categoryService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/categories/{id}", categoryEntity.getId()));
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.metadata.code", is(Code.NO_CONTENT.getCode())));
	}

	@DisplayName("카테고리가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenId_whenCallDeleteCategory_thenThrowCategoryNotFoundException() throws Exception {
		ResultActions action;
		
		willThrow(new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."})).given(categoryService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/categories/{id}", categoryEntity.getId()));
		
		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryNotFoundException));
	}

	@DisplayName("카테고리를 삭제하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenId_whenCallDeleteCategory_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		willDoNothing().given(categoryService).delete(any(Long.class));

		action = mockMvc.perform(delete("/api/v1/categories/{id}", categoryEntity.getId()));
		
		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}
	
	@DisplayName("카테고리 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallGetCategories_thenReturnCategories() throws Exception {
		ResultActions action;
		CategoryResponse category2;
		PageResponse<CategoryResponse> page;
		MultiValueMap<String, String> params;
		
		category2 = new CategoryResponse().name("카테고리2");

		page = PageResponse.handleResponse(List.of(category1, category2), pagination.getPageSize(), pagination.getPageNo(), 2, 1, true, true);

		given(categoryService.findAll(any(PaginationUtil.class))).willReturn(page);
		
		params = new LinkedMultiValueMap<String, String>();
		params.add("pageNo", String.valueOf(pagination.getPageNo()));
		params.add("pageSize", String.valueOf(pagination.getPageSize()));
		params.add("sortBy", pagination.getSortBy());
		params.add("sortDir", pagination.getSortDir());
		
		action = mockMvc.perform(
						get("/api/v1/categories")
						.params(params)
						.characterEncoding(StandardCharsets.UTF_8)
				);
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.content.size()", is(page.getContent().size())));
	}
	
	@DisplayName("카테고리를 수정하는데 성공한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCategoryUpdate_whenCallUpdateCategory_thenReturnCategory() throws Exception {
		ResultActions action;
		
		given(categoryService.update(any(Long.class), any(CategoryUpdateRequest.class))).willAnswer((answer) -> {
			categoryEntity.name(categoryUpdate.getName());
			
			category1 = CategoryMapper.INSTANCE.toDto(categoryEntity);
			
			return category1;
		});
		
		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", categoryEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.name", is(category1.getName())));
	}

	@DisplayName("이름이  짧아 카테고리를 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCategoryUpdate_whenCallUpdateCategory_thenThrowBadRqeustExceptionForName() throws Exception {
		ResultActions action;
		
		given(categoryService.update(any(Long.class), any(CategoryUpdateRequest.class))).willAnswer((answer) -> {
			categoryEntity.name(categoryUpdate.getName());
			
			category1 = CategoryMapper.INSTANCE.toDto(categoryEntity);
			
			return category1;
		});
		
		categoryUpdate.name("가");
		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", categoryEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);
		
		action
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@DisplayName("카테고리가 존재하지 않아 수정하는데 실패한다.")
	@Test
	@WithMockCustomAdmin
	public void givenCategoryUpdate_whenCallUpdateCategory_thenThrowCategoryNotFoundException() throws Exception {
		ResultActions action;
		
		given(categoryService.update(any(Long.class), any(CategoryUpdateRequest.class))).willThrow(new CategoryNotFoundException(Code.NOT_FOUND, new String[] {"카테고리가 존재하지 않습니다."}));
		
		action = mockMvc.perform(
						patch("/api/v1/categories/{id}", categoryEntity.getId())
						.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
				);

		action
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(result -> assertTrue(result.getResolvedException() instanceof CategoryNotFoundException));
	}
	
	@DisplayName("카테고리를 수정하는데 필요한 권한이 없어 실패한다.")
	@Test
	@WithMockCustomUser
	public void givenCategoryUpdate_whenCallUpdateCategory_thenThrowUnauthorizedUserException() throws Exception {
		ResultActions action;
		
		given(categoryService.update(any(Long.class), any(CategoryUpdateRequest.class))).willAnswer((answer) -> {
			categoryEntity.name(categoryUpdate.getName());
			
			category1 = CategoryMapper.INSTANCE.toDto(categoryEntity);
			
			return category1;
		});
		
		action = mockMvc.perform(patch("/api/v1/categories/{id}", categoryEntity.getId())
										.content(SerializeDeserializeUtil.serializeToString(categoryUpdate))
										.characterEncoding(StandardCharsets.UTF_8)
										.contentType(MediaType.APPLICATION_JSON));

		action
		.andDo(print())
		.andExpect(status().isForbidden());
	}		
}