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

import com.whooa.blog.category.dto.CategoryDTO.CategoryCreateRequest;
import com.whooa.blog.category.dto.CategoryDTO.CategoryResponse;
import com.whooa.blog.category.dto.CategoryDTO.CategoryUpdateRequest;
import com.whooa.blog.category.service.CategoryService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.util.PaginationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(
	name = "카테고리 API"
)
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	private CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@Operation(
		summary = "카테고리 생성"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest categoryCreate) {
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), categoryService.create(categoryCreate), new String[] {"카테고리를 생성했습니다."});
	}
	
	@Operation(
		summary = "카테고리 삭제"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<CategoryResponse> deleteCategory(@PathVariable Long id) {
		categoryService.delete(id);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"카테고리를 삭제했습니다."});
	}	

	@Operation(
		summary = "카테고리 조회"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.find(id), new String[] {"카테고리를 조회했습니다."});
	}

	@Operation(
		summary = "카테고리 목록 조회"
	)	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<CategoryResponse>> getCategories(PaginationUtil paginationUtil) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.findAll(paginationUtil), new String[] {"카테고리 목록을 조회했습니다."});
	}

	@Operation(
		summary = "카테고리 수정"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{id}")
	public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest categoryUpdate) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), categoryService.update(id, categoryUpdate), new String[] {"카테고리을 수정했습니다."});
	}
}