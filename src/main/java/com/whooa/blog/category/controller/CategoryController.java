package com.whooa.blog.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.category.dto.CategoryDto.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDto.CategorySearchRequest;
import com.whooa.blog.category.dto.CategoryDto.CategoryUpdateRequest;
import com.whooa.blog.category.service.CategoryService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.util.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(description = "카테고리 생성/조회/목록/검색/수정/삭제를 수행하는 카테고리 컨트롤러", name = "카테고리 API")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	private CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "카테고리 생성", method = "POST", summary = "카테고리 생성")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "카테고리 생성 성공", responseCode = "201")
	@Parameter(example = "운영체제", description = "이름", name = "name")
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest categoryCreate) {
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), categoryService.create(categoryCreate), new String[] {"카테고리를 생성했습니다."});
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 카테고리 삭제", method = "DELETE", summary = "카테고리 삭제")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "카테고리 삭제 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<CategoryResponse> deleteCategory(@PathVariable Long id) {
		categoryService.delete(id);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"카테고리를 삭제했습니다."});
	}	

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 카테고리 조회", method = "GET", summary = "카테고리 조회")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "카테고리 조회 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.find(id), new String[] {"카테고리를 조회했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "카테고리 목록", method = "GET", summary = "카테고리 목록")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "카테고리 목록 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<CategoryResponse>> getCategories(PaginationParam paginationParam) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.findAll(paginationParam), new String[] {"카테고리 목록을 조회했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "검색어를 만족하는 카테고리 목록", method = "GET", summary = "카테고리 검색")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "카테고리 검색 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/search")
	public ApiResponse<PageResponse<CategoryResponse>> searchCategories(CategorySearchRequest categorySearch, PaginationParam paginationParam) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.search(categorySearch, paginationParam), new String[] {"검색어를 만족하는 카테고리를 검색했습니다."});
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 카테고리 수정", method = "PATCH", summary = "카테고리 수정")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "카테고리 수정 성공", responseCode = "200")
	@Parameter(example = "알고리즘", description = "이름", name = "name")
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{id}")
	public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest categoryUpdate) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.update(id, categoryUpdate), new String[] {"카테고리을 수정했습니다."});
	}
}