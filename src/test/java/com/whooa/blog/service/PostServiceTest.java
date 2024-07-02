package com.whooa.blog.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
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
	private PostServiceImpl postServiceImpl;

	private PostEntity postEntity1;
	private CategoryEntity categoryEntity1;
	private UserEntity userEntity;
	
	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;
	
	private File file;
    private MockMultipartFile[] uploadFiles;
    
	private PaginationUtil paginationUtil;
	
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeEach
	public void setUp() {
		/*
		 * mock() 메서드로 PostRepository 인터페이스의 모의 객체를 생성한다.
	     * postRepository = Mockito.mock(PostRepository.class);
		 * postServiceImpl = new PostServiceImpl(postRepository);
		 */
		String title = "테스트 제목";
		String content = "테스트 내용";
		
		categoryEntity1 = new CategoryEntity().name("테스트 카테고리");
		
		userEntity = new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER);
		
		postEntity1 = new PostEntity()
				.content(content)
				.title(title)
				.category(categoryEntity1);

		postCreate = new PostCreateRequest()
				.categoryName(categoryEntity1.getName())
				.content(content)
				.title(title);
		
		postUpdate = new PostUpdateRequest()
				.categoryName("실전 카테고리")
				.content("실전 내용")
				.title("실전 제목");

		uploadFiles = new MockMultipartFile[] {
				new MockMultipartFile("test1", "test1.txt", "text/plain", "테스트 파일1".getBytes(StandardCharsets.UTF_8)),
				//new MockMultipartFile("test2", "test2.txt", "text/plain", "테스트 파일2".getBytes(StandardCharsets.UTF_8))
		};
		
		file = new File("txt", uploadFiles[0].getContentType(), uploadFiles[0].getOriginalFilename(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", uploadFiles[0].getSize());

		paginationUtil = new PaginationUtil();
		
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
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity1);
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		
		PostResponse post = postServiceImpl.create(postCreate, null, userDetailsImpl);
				
		assertNotNull(post);
		assertNull(post.getFiles());
		assertEquals(post.getTitle(), postEntity1.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트(파일 O)를 생성하는데 성공한다.")
	@Test
	public void givenPostCreate_whenCallCreate_thenReturnPostWithFiles() {
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity1);
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);
		
		PostResponse post = postServiceImpl.create(postCreate, uploadFiles, userDetailsImpl);
				
		assertNotNull(post);
		assertNotNull(post.getFiles());
		assertEquals(post.getFiles().get(0).getName(), file.getName());
		assertEquals(post.getTitle(), postEntity1.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
	}	
	
	@DisplayName("포스트를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			postServiceImpl.create(null, uploadFiles, userDetailsImpl);	
		});
		
		then(postRepository).should(times(0)).save(any());
		then(categoryRepository).should(times(0)).findByName(any(String.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));		
	}	
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnPosts() {
		PostEntity postEntity2 = new PostEntity()
				.content("실전 내용")
				.title("실전 제목")
				.category(new CategoryEntity().name("실전 카테고리"));
		
		given(postRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<PostEntity>(List.of(postEntity1, postEntity2)));

		PageResponse<PostResponse> pageRresponse = postServiceImpl.findAll(paginationUtil);
					
		assertNotNull(pageRresponse.getContent());
		assertEquals(pageRresponse.getTotalElements(), 2);
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
	}
	
	@DisplayName("포스트 목록(카테고리 아이디)을 조회하는데 성공한다.")
	@Test
	public void givenCategoryIdAndPagination_whenCallFindAllByCategoryId_thenReturnPosts() {
		CategoryEntity categoryEntity2 = new CategoryEntity().name("실전 카테고리");
		
		PostEntity postEntity2 = new PostEntity()
				.content("실전 내용")
				.title("실전 제목")
				.category(categoryEntity2);
		
		given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity1));
		given(postRepository.findByCategoryId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<PostEntity>(List.of(postEntity2)));

		PageResponse<PostResponse> pageRresponse = postServiceImpl.findAllByCategoryId(categoryEntity2.getId(), paginationUtil);
					
		assertNotNull(pageRresponse.getContent());
		assertEquals(pageRresponse.getTotalElements(), 1);
		
		then(postRepository).should(times(1)).findByCategoryId(any(Long.class), any(Pageable.class));
		then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFind_thenReturnPost() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		PostResponse post = postServiceImpl.find(postEntity1.getId());
		
		assertNotNull(post);
		assertEquals(post.getId(), postEntity1.getId());
		
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFind_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			postServiceImpl.find(1L);
		});
		
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		/* void 반환타입을 가지는 메서드를 스텁할 경우 willDoNothing() 메서드를 사용한다. */
		willDoNothing().given(postRepository).delete(any(PostEntity.class));		
		
		postServiceImpl.delete(postEntity1.getId());
						
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postRepository).should(times(1)).delete(any(PostEntity.class));
	}
	
	@DisplayName("포스트(파일 X)를 갱신하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPost() {
		CategoryEntity categoryEntity2 = new CategoryEntity().name("실전 카테고리");
		PostEntity postEntity2 = new PostEntity()
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle())
				.category(categoryEntity2);
		
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));

		PostResponse post = postServiceImpl.update(postEntity1.getId(), postUpdate, null, userDetailsImpl);
		
		assertNotNull(post);
		assertNull(post.getFiles());
		assertEquals(post.getTitle(), postUpdate.getTitle());
		assertEquals(post.getContent(), postUpdate.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트(파일 O)를 갱신하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPostWithFiles() {
		CategoryEntity categoryEntity2 = new CategoryEntity().name("실전 카테고리");
		PostEntity postEntity2 = new PostEntity()
				.content(postUpdate.getContent())
				.title(postUpdate.getTitle())
				.category(categoryEntity2);

		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);

		PostResponse post = postServiceImpl.update(postEntity1.getId(), postUpdate, uploadFiles, userDetailsImpl);
		
		assertNotNull(post);
		assertNotNull(post.getFiles());
		assertEquals(post.getTitle(), postUpdate.getTitle());
		assertEquals(post.getContent(), postUpdate.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
	}	
}