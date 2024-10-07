package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.nio.charset.StandardCharsets;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.category.dto.CategoryDto.CategoryResponse;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
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
import com.whooa.blog.post.mapper.PostMapper;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.post.service.impl.PostServiceImpl;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotMatchedException;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

/* Mockito 클래스를 확장해서 의존성을 모의하기 위해 주석을 사용하는 것을 이해한다. */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostServiceTest {
	/*
	 * @InjectMocks 어노테이션을 사용하여 모의 객체를 다른 모의 객체에 주입한다. 
	 * @InjectMocks 어노테이션은 모의 객체를 생성하고 그 안에 @Mock 어노테이션으로 지정된 모든 모의 객체들을 주입한다. 
	 */
	@InjectMocks
	private PostServiceImpl postServiceImpl;
	
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
	@Mock
	private PostMapper postMapper;
		
	private PostEntity postEntity1;
	private CategoryEntity categoryEntity1;
	private UserEntity userEntity1;

	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;
	private PostResponse post;
	private CategoryResponse category;
	
	private File file;
    private MockMultipartFile[] uploadFiles;
    	
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	public void setUpAll() {
		/*
		 * mock() 메서드로 PostRepository 인터페이스의 모의 객체를 생성한다.
	     * postRepository = Mockito.mock(PostRepository.class);
		 * postServiceImpl = new PostServiceImpl(postRepository);
		 */
		postCreate = new PostCreateRequest()
							.categoryName("카테고리1")
							.content("포스트1")
							.title("포스트1");
		
		postUpdate = new PostUpdateRequest()
							.categoryName("카테고리2")
							.content("포스트2")
							.title("포스트2");

		uploadFiles = new MockMultipartFile[] {
			new MockMultipartFile("test1", "test1.txt", "text/plain", "테스트 파일1".getBytes(StandardCharsets.UTF_8)),
			//new MockMultipartFile("test2", "test2.txt", "text/plain", "테스트 파일2".getBytes(StandardCharsets.UTF_8))
		};
		
		file = new File("txt", uploadFiles[0].getContentType(), uploadFiles[0].getOriginalFilename(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", uploadFiles[0].getSize());
		
		categoryEntity1 = CategoryEntity.builder()
							.name("카테고리1")
							.build();
		
		userEntity1 = UserEntity.builder()
						.email("user1@naver.com")
						.name("사용자1")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build();

		userDetailsImpl = new UserDetailsImpl(userEntity1);
	}
	
	@BeforeEach
	public void setUpEach() {
		postEntity1 = PostEntity.builder()
						.content("포스트1")
						.title("포스트1")
						.category(categoryEntity1)
						.user(userEntity1)
						.build();

		category = CategoryResponse.builder()
						.id(categoryEntity1.getId())
						.name(categoryEntity1.getName())
						.build();
		
		post = PostResponse.builder()
					.id(postEntity1.getId())
					.content(postEntity1.getContent())
					.title(postEntity1.getTitle())
					.category(category)
					.files(List.of(file))
					.build();
	}

	@DisplayName("포스트(파일 X)를 생성하는데 성공한다.")
	@Test
	public void givenPostCreate_whenCallCreate_thenReturnPost() {
		PostResponse createdPost;
		
		/*
		 * org.mockito.exceptions.misusing.PotentialStubbingProblem 오류.
		 * Mockito 2.20부터 lenient() 메서드로 Mockito의 기본 행동인 strict stub을 변경한다.
		 * BDDMockito 클래스는 메소드를 설정된 응답을 반환하도록 스텁(stub)한다.
		 * BDDMockito.lenient().when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity); 
		 */
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity1);
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		given(postMapper.toEntity(any(PostCreateRequest.class))).willReturn(postEntity1);
		given(postMapper.fromEntity(any(PostEntity.class))).willReturn(post);

		createdPost = postServiceImpl.create(postCreate, null, userDetailsImpl);

		assertNull(post.getFiles());
		assertEquals(createdPost.getTitle(), post.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
		then(postMapper).should(times(1)).toEntity(any(PostCreateRequest.class));
		then(postMapper).should(times(1)).fromEntity(any(PostEntity.class));
	}
	
	@DisplayName("포스트(파일 O)를 생성하는데 성공한다.")
	@Test
	public void givenPostCreate_whenCallCreate_thenReturnPostWithFiles() {
		PostResponse createdPost;

		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity1);
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);
		given(postMapper.toEntity(any(PostCreateRequest.class))).willReturn(postEntity1);
		given(postMapper.fromEntity(any(PostEntity.class))).willReturn(post);
		
		createdPost = postServiceImpl.create(postCreate, uploadFiles, userDetailsImpl);
			
		assertNotNull(post.getFiles());
		assertEquals(file.getName(), post.getFiles().get(0).getName());
		assertEquals(createdPost.getTitle(), post.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
		then(postMapper).should(times(1)).toEntity(any(PostCreateRequest.class));
		then(postMapper).should(times(1)).fromEntity(any(PostEntity.class));
	}	

	@DisplayName("카테고리가 존재하지 않아 포스트를 생성하는데 실패한다.")
	@Test
	public void givenPostCreate_whenCallCreate_thenThrowCategoryNotFoundException() {
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			postServiceImpl.create(postCreate, uploadFiles, userDetailsImpl);	
		});
		
		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
		then(postMapper).should(times(0)).toEntity(any(PostCreateRequest.class));
		then(postMapper).should(times(0)).fromEntity(any(PostEntity.class));
	}
	
	@DisplayName("포스트를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallCreate_thenThrowNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			postServiceImpl.create(null, uploadFiles, userDetailsImpl);	
		});
		
		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(categoryRepository).should(times(0)).findByName(any(String.class));
		then(userRepository).should(times(0)).findById(any(Long.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
		then(postMapper).should(times(0)).toEntity(any(PostCreateRequest.class));
		then(postMapper).should(times(0)).fromEntity(any(PostEntity.class));
	}	

	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		/* void 반환타입을 가지는 메서드를 스텁할 경우 willDoNothing() 메서드를 사용한다. */
		willDoNothing().given(postRepository).delete(any(PostEntity.class));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		
		postServiceImpl.delete(postEntity1.getId(), userDetailsImpl);

		then(postRepository).should(times(1)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			postServiceImpl.delete(postEntity1.getId(), userDetailsImpl);
		});

		then(postRepository).should(times(0)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("포스트를 작성한 사용자와 일치하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowUserNotMatchedException() {
		UserEntity userEntity2;
		
		userEntity2 = UserEntity.builder()
						.id(2L)
						.email("user2@naver.com")
						.name("사용자2")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build();
				
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		
		assertThrows(UserNotMatchedException.class, () -> {
			postServiceImpl.delete(postEntity1.getId(), new UserDetailsImpl(userEntity2));
		});
		
		then(postRepository).should(times(0)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}

	@DisplayName("포스트를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFind_thenReturnPost() {
		PostResponse foundPost;
		
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(postMapper.fromEntity(any(PostEntity.class))).willReturn(post);

		foundPost = postServiceImpl.find(postEntity1.getId());
		
		assertEquals(foundPost.getId(), post.getId());
		
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFind_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			postServiceImpl.find(postEntity1.getId());
		});
		
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(postMapper).should(times(1)).fromEntity(any(PostEntity.class));
	}
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnPosts() {
		PageResponse<PostResponse> page;
		PostEntity postEntity2;
		
		postEntity2 = PostEntity.builder()
						.content("포스트2")
						.title("포스트2")
						.category(CategoryEntity.builder().name("카테고리2").build())
						.user(userEntity1)
						.build();

		given(postRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<PostEntity>(List.of(postEntity1, postEntity2)));
		given(postMapper.fromEntity(any(PostEntity.class))).willReturn(post);
		
		page = postServiceImpl.findAll(new PaginationUtil());
					
		assertEquals(2, page.getTotalElements());
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
		then(postMapper).should(times(2)).fromEntity(any(PostEntity.class));

	}
	
	@DisplayName("포스트 목록을 조회(카테고리 아이디)하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAllByCategoryId_thenReturnPosts() {
		CategoryEntity categoryEntity2;
		PageResponse<PostResponse> page;
		PostEntity postEntity2;
		
		categoryEntity2 = CategoryEntity.builder()
							.name("카테고리2")
							.build();
		
		postEntity2 = PostEntity.builder()
						.content("포스트2")
						.title("포스트2")
						.category(categoryEntity2)
						.user(userEntity1)
						.build();
			
		given(postRepository.findByCategoryId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<PostEntity>(List.of(postEntity2)));
		given(postMapper.fromEntity(any(PostEntity.class))).willReturn(post);

		page = postServiceImpl.findAllByCategoryId(categoryEntity2.getId(), new PaginationUtil());
					
		assertEquals(1, page.getTotalElements());
		
		then(postRepository).should(times(1)).findByCategoryId(any(Long.class), any(Pageable.class));
		//then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPost(PostEntity postEntity1, CategoryEntity categoryEntity1, UserEntity userEntity) {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = CategoryEntity.builder()
							.name("카테고리2")
							.build();
		
		postEntity2 = PostEntity.builder()
						.content(postUpdate.getContent())
						.title(postUpdate.getTitle())
						.category(categoryEntity2)
						.user(userEntity)
						.build();
		
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));

		post = postServiceImpl.update(postEntity1.getId(), postUpdate, null, userDetailsImpl);
		
		assertNull(post.getFiles());
		assertEquals(postUpdate.getTitle(), post.getTitle());
		assertEquals(postUpdate.getContent(), post.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트(파일 O)를 수정하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPostWithFiles(PostEntity postEntity1, CategoryEntity categoryEntity1, UserEntity userEntity) {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = CategoryEntity.builder()
							.name("카테고리2")
							.build();
		
		postEntity2 = PostEntity.builder()
						.content(postUpdate.getContent())
						.title(postUpdate.getTitle())
						.category(categoryEntity2)
						.user(userEntity)
						.build();

		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);

		post = postServiceImpl.update(postEntity1.getId(), postUpdate, uploadFiles, userDetailsImpl);
		
		assertNotNull(post.getFiles());
		assertEquals(postUpdate.getTitle(), post.getTitle());
		assertEquals(postUpdate.getContent(), post.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenThrowPostNotFoundException(PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			postServiceImpl.update(postEntity.getId(), postUpdate, null, userDetailsImpl);			
		});
		
		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(0)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 포스트를 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenThrowCategoryNotFoundException(PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> {
			postServiceImpl.update(postEntity.getId(), postUpdate, null, userDetailsImpl);			
		});

		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트를 생성한 사용자와 일치하지 않아 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenUserNotMatchedException(PostEntity postEntity, CategoryEntity categoryEntity, UserEntity userEntity1) {
		UserEntity userEntity2;
		
		userEntity2 = UserEntity.builder()
						.id(2L)
						.email("user2@naver.com")
						.name("사용자2")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build();
		
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity));

		assertThrows(UserNotMatchedException.class, () -> {
			postServiceImpl.update(postEntity.getId(), postUpdate, null, new UserDetailsImpl(userEntity2));			
		});

		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
}