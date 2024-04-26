package com.whooa.blog.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.service.PostService;

import jakarta.validation.Valid;

/*
 * @Controller 어노테이션은 클래스를 Spring MVC 컨트롤러로 만든다. 
 * @ResponseBody 어노테이션은 Java 객체를 JSON으로 변환하고 JSON이 다시 HTTP 응답으로 변환된다.
 * @RestController 어노테이션은 내부적으로 두 어노테이션을 사용한다.
 */
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
	private PostService postService;
	
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
	/*
	 * @Valid 어노테이션은 Hibernate 검증자(validator)를 활성화한다.
	 * 
	 * @ResponseStatus 어노테이션은 컨트롤러에서 지정된 HTTP 상태 코드로 응답하도록 하는 역할을 한다.
	 * 
	 * @RequestBody 어노테이션은 데이터를 JSON 형식으로 전달받기 때문에 만약 파일을 본문으로 받게 된다면 원하는 결과를 얻을 수 없다. 
	 * @RequestParam 어노테이션은 또한 기본적으로 문자열 데이터를 처리하는데 사용되므로 미디어 파일과 같은 이진 데이터를 받아오는 것은 적절하지 않다.
	 * @RequestPart 어노테이션을 사용해서 이진 데이터를 받는다.
	 * 
	 * @RequestPart 어노테이션은 HTTP 요청 본문에 multipart/form-data가 포함되어 있는 경우에 사용한다. 
	 * MultipartFile이 포함되어 있는 경우 MultipartResolver가 동작하여 역직렬화를 한다. 만약 MultipartFile이 포함되어있지 않다면, @RequestBody와 마찬가지로 동작한다.
	 * 
	 * consumes 속성은 해당 엔드포인트에서 수신되는 요청의 미디어 타입을 지정하는데 사용된다. 즉, 클라이언트가 요청 본문에 담아 보내는 데이터의 형식을 나타낸다.
	 */
	@ResponseStatus(value = HttpStatus.CREATED)
	//@PreAuthorize("hasRole('ADMIN')")
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ApiResponse<PostResponse> createPost(@Valid @RequestPart(name = "post") PostCreateRequest postCreate, @RequestPart(name = "files", required = false) MultipartFile[] uploadFiles) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), postService.create(postCreate, uploadFiles), new String[] {"포스트를 생성했습니다."});
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<PostResponse>> getPosts(PageDto pageDto) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postService.findAll(pageDto), new String[] {"포스트를 조회했습니다."});
	}
	
	/* @PathVariable 어노테이션은 메서드 인자를 URI 템플릿 변수의 값에 바인딩한다. */
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<PostResponse> getPost(@PathVariable Long id) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postService.find(id), new String[] {"포스트 목록을 조회했습니다."});
	}
	
	/* @RequestBody 어노테이션은 내부적으로 Spring이 제공하는 HttpMessageConverter를 사용하여 JSON을 Java 객체로 변환한다. */
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(value = HttpStatus.OK)
	@PutMapping("/{id}")
	public ApiResponse<PostResponse> updatePost(@Valid @RequestBody PostUpdateRequest postUpdate, @PathVariable Long id) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postService.update(postUpdate, id), new String[] {"포스트를 수정했습니다."});
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<PostResponse> deletePost(@PathVariable Long id) {
		postService.delete(id);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"포스트가 삭제되었습니다."});
	}	
}