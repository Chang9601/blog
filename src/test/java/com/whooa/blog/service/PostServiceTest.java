package com.whooa.blog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.impl.PostServiceImpl;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/* Mockito 클래스를 확장해서 의존성을 모의하기 위해 주석을 사용하는 것을 이해한다. */
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
	/*
	 * @Mock 어노테이션을 사용하여 모의 객체를 생성할 수 있다. 
	 * mock() 메서드를 여러 번 호출하는 대신 모의 객체를 여러 곳에서 사용하고자 할 때 유용하다.
	 */
	@Mock
	private PostRepository postRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private UserRepository userRepository;
	@Mock	
	private FileService fileService;

	/*
	 * @InjectMocks 어노테이션을 사용하여 모의 객체를 다른 모의 객체에 주입한다. 
	 * @InjectMocks 어노테이션은 모의 객체를 생성하고 그 안에 @Mock 어노테이션으로 지정된 모든 모의 객체들을 주입한다. 
	 */
	@InjectMocks
	private PostServiceImpl postService;

	//MultipartFile[] uploadFiles;
	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;
	
	private PostEntity postEntity;
	private CategoryEntity categoryEntity;
	private UserEntity userEntity;
	
	private File file;
	
    private MockMultipartFile[] uploadFiles; 
	private PaginationUtil paginationUtil;
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeEach
	public void setUp() {
		/*
		 * mock() 메서드로 PostRepository 인터페이스의 모의 객체를 생성한다.
	     * postRepository = Mockito.mock(PostRepository.class);
		 * postService = new PostServiceImpl(postRepository);
		 */
		
		String title = "테스트 제목";
		String content = "테스트 내용";
		
		categoryEntity = new CategoryEntity().name("테스트 카테고리");
		
		userEntity = new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER);
		
		postEntity = new PostEntity()
				.content(content)
				.title(title)
				.category(categoryEntity);

		postCreate = new PostCreateRequest()
				.categoryName("테스트 카테고리")
				.content(content)
				.title(title);

		uploadFiles = new MockMultipartFile[] {
				new MockMultipartFile("test1", "test1.txt", "text/plain", "테스트 파일1".getBytes(StandardCharsets.UTF_8)),
				//new MockMultipartFile("test2", "test2.txt", "text/plain", "테스트 파일2".getBytes(StandardCharsets.UTF_8))
		};
		
		file = new File("txt", uploadFiles[0].getContentType(), uploadFiles[0].getOriginalFilename(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", uploadFiles[0].getSize());
		
		/* UserDetailsImpl 생성하기. */
		userDetailsImpl = new UserDetailsImpl(userEntity);
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities()));
		SecurityContextHolder.setContext(securityContext);
	}
	
	@DisplayName("포스트(파일 X)를 생성하는데 성공한다.")
	@Test
	public void givenPostCreate_whenCallCreate_thenReturnPost() {
		/*
		 * org.mockito.exceptions.misusing.PotentialStubbingProblem 오류.
		 * Mockito 2.20부터 lenient() 메서드로 Mockito의 기본 행동인 strict stub을 변경한다.
		 * BDDMockito 클래스는 메소드를 설정된 응답을 반환하도록 스텁(stub)한다.
		 * BDDMockito.lenient().when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity); 
		 */
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		
		PostResponse post = postService.create(postCreate, null, userDetailsImpl);
				
		assertNotNull(post);
		assertEquals(post.getTitle(), postEntity.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트(파일 O)를 생성하는데 성공한다.")
	@Test
	public void givenPostCreate_whenCallCreate_thenReturnPostWithFiles() {
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);
		
		PostResponse post = postService.create(postCreate, uploadFiles, userDetailsImpl);
				
		assertNotNull(post);
		assertEquals(post.getFiles().get(0).getName(), file.getName());
		assertEquals(post.getTitle(), postEntity.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
	}	
	
	@DisplayName("포스트를 작성하지 않아 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			postService.create(null, uploadFiles, userDetailsImpl);	
		});
		
		then(postRepository).should(times(0)).save(any());
		then(categoryRepository).should(times(0)).findByName(any(String.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));		
	}	
//	
//	@DisplayName("포스트 목록을 조회하는데 성공한다.")
//	@Test
//	public void givenPage_whenCallFindAll_thenReturnAllPosts() {
//		PostEntity postEntity2 = new PostEntity(2L, "실전", "실전을 위한 포스트");
//		
//		Pageable pageable = PageRequest.of(page.getPageNo(), page.getPageSize());
//		Page<PostEntity> postEntities = new PageImpl<>(List.of(postEntity, postEntity2), pageable, 2);
//		
//		given(postRepository.findAll(any(Pageable.class))).willReturn(postEntities);
//		
//		PageResponse<PostResponse> response = postService.findAll(page);
//					
//		assertNotNull(response.getContent());
//		assertEquals(response.getTotalElements(), 2);
//		
//		then(postRepository).should(times(1)).findAll(any(Pageable.class));
//	}
//
//	@DisplayName("포스트가 존재하지 않아서 포스트 목록을 조회하는데 실패한다.")
//	@Test
//	public void givenPage_whenCallFindAll_thenReturnNothing() {
//		Pageable pageable = PageRequest.of(page.getPageNo(), page.getPageSize());
//		Page<PostEntity> postEntities = new PageImpl<>(List.of(), pageable, 0);
//		
//		given(postRepository.findAll(any(Pageable.class))).willReturn(postEntities);
//
//		PageResponse<PostResponse> response = postService.findAll(page);
//
//		assertEquals(response.getContent(), Collections.emptyList());
//		assertEquals(response.getTotalElements(), 0);
//		
//		then(postRepository).should(times(1)).findAll(any(Pageable.class));
//	}
//		
//	@DisplayName("포스트를 조회하는데 성공한다.")
//	@Test
//	public void givenId_whenCallFind_thenReturnPost() {
//		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
//
//		PostResponse post = postService.find(eId);
//		
//		assertNotNull(post);
//		assertEquals(post.getId(), postEntity.getId());
//		
//		then(postRepository).should(times(1)).findById(any(Long.class));
//	}
//	
//	@DisplayName("포스트가 존재하지 않아서 포스트를 조회하는데 실패한다.")
//	@Test
//	public void givenId_whenCallFind_thenThrowPostNotFoundException() {
//		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
//		
//		assertThrows(PostNotFoundException.class, () -> {
//			postService.find(dneId);
//		});
//	}
//	
//	@DisplayName("포스트를 갱신하는데 성공한다.")
//	@Test
//	public void givenPostUpdate_whenCallUpdate_thenReturnPost() {
//		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
//		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity);
//		
//		PostResponse post = postService.update(postUpdate, eId);
//		
//		assertNotNull(post);
//		assertEquals(post.getTitle(), postUpdate.getTitle());
//		assertEquals(post.getContent(), postUpdate.getContent());
//
//		then(postRepository).should(times(1)).findById(any(Long.class));
//		then(postRepository).should(times(1)).save(any(PostEntity.class));
//	}
//	
//	@DisplayName("포스트가 존재하지 않아서 포스트를 갱신하는데 실패한다.")
//	@Test
//	public void givenPostUpdate_whenCallUpdate_thenThrowPostNotFoundException() {
//		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
//
//		assertThrows(PostNotFoundException.class, () -> {
//			postService.update(postUpdate, dneId);
//		});
//		
//		/*
//		  예외를 던지고 난 후 제어가 여기로 오지 않을 것이라는 것을 검증한다.
//		  Mockito.verify(postRepository, never()).save(any(PostEntity.class));
//		*/	
//		then(postRepository).should(times(1)).findById(any(Long.class));
//		then(postRepository).should(times(0)).save(any(PostEntity.class));
//	}
//	
//	@DisplayName("포스트를 삭제하는데 성공한다.")
//	@Test
//	public void givenId_whenCallDelete_thenReturnNothing() {
//		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
//		/* void 반환타입을 가지는 메서드를 스텁할 경우 willDoNothing() 메서드를 사용한다. */
//		willDoNothing().given(postRepository).delete(any(PostEntity.class));		
//		
//		postService.delete(eId);
//						
//		then(postRepository).should(times(1)).findById(any(Long.class));
//		then(postRepository).should(times(1)).delete(any(PostEntity.class));
//	}
//	
//	@DisplayName("포스트가 존재하지 않아서 포스트를 삭제하는데 실패한다.")
//	@Test
//	public void givenId_whenCallDelete_thenThrowPostNotFoundException() {
//		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
//				
//		assertThrows(PostNotFoundException.class, () -> {
//			postService.delete(dneId);
//		});
//		
//		then(postRepository).should(times(1)).findById(any(Long.class));
//		then(postRepository).should(times(0)).delete(any(PostEntity.class));
//	}		
}