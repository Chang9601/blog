package com.whooa.blog.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.admin.service.AdminUserService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserSearchRequest;
import com.whooa.blog.util.PaginationParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// TODO: 사용자 조회/목록/검색에서 포스트, 댓글까지 가져오는 API
@Tag(description = "사용자 조회/목록/검색/수정/삭제를 수행하는 관리자 사용자 컨트롤러", name = "관리자(사용자) API")
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
	private AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 사용자 삭제", method = "DELETE", summary = "사용자 삭제")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "사용자 삭제 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<UserResponse> deleteUser(@PathVariable Long id) {
		adminUserService.delete(id);
		
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"사용자를 삭제했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 사용자 조회", method = "GET", summary = "사용자 조회")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "사용자 조회 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.find(id), new String[] {"사용자를 조회했습니다."});
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "사용자 목록", method = "GET", summary = "사용자 목록")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "사용자 목록 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<UserResponse>> getUsers(PaginationParam paginationParam) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.findAll(paginationParam), new String[] {"사용자 목록을 조회했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "검색어를 만족하는 사용자 목록", method = "GET", summary = "사용자 검색")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "사용자 검색 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/search")
	public ApiResponse<PageResponse<UserResponse>> searchUsers(UserSearchRequest userSearch, PaginationParam paginationParam) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.search(userSearch, paginationParam), new String[] {"검색어를 만족하는 사용자를 검색했습니다."});
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 사용자 수정", method = "PATCH", summary = "사용자 수정")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "사용자 수정 성공", responseCode = "200")
	@Parameters({
		@Parameter(description = "이메일", example = "user1@naver.com", name = "email"),
		@Parameter(description = "이름", example = "사용자1", name = "name"),
		@Parameter(description = "비밀번호", example = "12341234Aa!@", name = "password"),
		@Parameter(description = "역할", example = "USER", name = "userRole"),
	})
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{id}")
	public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody(required = true) UserAdminUpdateRequest userAdminUpdate) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.update(id, userAdminUpdate), new String[] {"사용자를 수정했습니다."});
	}
}