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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
	private UserEntity userEntity1;
	
	private PostCreateRequest postCreate;
	private PostUpdateRequest postUpdate;
	private PostResponse post;
	
	private File file;
    private MockMultipartFile[] uploadFiles;
    
	private PaginationUtil pagination;
	
	private UserDetailsImpl userDetailsImpl;
	
	@BeforeAll
	public void setUpAll() {		
		pagination = new PaginationUtil();
		
		userDetailsImpl = new UserDetailsImpl(
			new UserEntity()
			.email("user1@user1.com")
			.name("사용자1 이름")
			.password("12345678Aa!@#$%")
			.userRole(UserRole.USER)
		);
	}
	
	@BeforeEach
	public void setUpEach() {
		/*
		 * mock() 메서드로 PostRepository 인터페이스의 모의 객체를 생성한다.
	     * postRepository = Mockito.mock(PostRepository.class);
		 * postServiceImpl = new PostServiceImpl(postRepository);
		 */
		String title, content;
		
		title = "포스트1";
		content = "포스트1";
		
		categoryEntity1 = new CategoryEntity().name("카테고리1");
		
		userEntity1 = new UserEntity()
					.email("user2@user2.com")
					.name("사용자2 이름")
					.password("12345678Aa!@#$%")
					.userRole(UserRole.USER);
			
		postEntity1 = new PostEntity()
					.content(content)
					.title(title)
					.category(categoryEntity1)
					.user(userEntity1);

		postCreate = new PostCreateRequest()
					.categoryName(categoryEntity1.getName())
					.content(content)
					.title(title);
		
		postUpdate = new PostUpdateRequest()
					.categoryName("카테고리2")
					.content("포스트2")
					.title("포스트2");

		uploadFiles = new MockMultipartFile[] {
				new MockMultipartFile("test1", "test1.txt", "text/plain", "테스트 파일1".getBytes(StandardCharsets.UTF_8)),
				//new MockMultipartFile("test2", "test2.txt", "text/plain", "테스트 파일2".getBytes(StandardCharsets.UTF_8))
		};
		
		file = new File("txt", uploadFiles[0].getContentType(), uploadFiles[0].getOriginalFilename(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", uploadFiles[0].getSize());
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
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		
		post = postServiceImpl.create(postCreate, null, userDetailsImpl);
				
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
		given(userRepository.findById(any(Long.class))).willReturn(Optional.of(userEntity1));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);
		
		post = postServiceImpl.create(postCreate, uploadFiles, userDetailsImpl);
			
		assertNotNull(post.getFiles());
		assertEquals(post.getFiles().get(0).getName(), file.getName());
		assertEquals(post.getTitle(), postEntity1.getTitle());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(userRepository).should(times(1)).findById(any(Long.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
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
		
		userEntity2 = new UserEntity()
					.email("user2@user2.com")	
					.name("사용자2")
					.password("12345678Aa!@#$%")
					.userRole(UserRole.USER);
		userEntity2.setId(2L);
				
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
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));

		post = postServiceImpl.find(postEntity1.getId());
		
		assertEquals(post.getId(), postEntity1.getId());
		
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
	}
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnPosts() {
		PageResponse<PostResponse> page;
		PostEntity postEntity2;
		
		postEntity2 = new PostEntity()
					.content("포스트2")
					.title("포스트2")
					.category(new CategoryEntity().name("카테고리2"))
					.user(userEntity1);

		given(postRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<PostEntity>(List.of(postEntity1, postEntity2)));

		page = postServiceImpl.findAll(pagination);
					
		assertEquals(page.getTotalElements(), 2);
		
		then(postRepository).should(times(1)).findAll(any(Pageable.class));
	}
	
	@DisplayName("포스트 목록을 조회(카테고리 아이디)하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAllByCategoryId_thenReturnPosts() {
		CategoryEntity categoryEntity2;
		PageResponse<PostResponse> page;
		PostEntity postEntity2;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		postEntity2 = new PostEntity()
					.content("포스트2")
					.title("포스트2")
					.category(categoryEntity2)
					.user(userEntity1);
			
		given(postRepository.findByCategoryId(any(Long.class), any(Pageable.class))).willReturn(new PageImpl<PostEntity>(List.of(postEntity2)));
		//given(categoryRepository.findById(any(Long.class))).willReturn(Optional.of(categoryEntity1));

		page = postServiceImpl.findAllByCategoryId(categoryEntity2.getId(), pagination);
					
		assertEquals(page.getTotalElements(), 1);
		
		then(postRepository).should(times(1)).findByCategoryId(any(Long.class), any(Pageable.class));
		//then(categoryRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPost() {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		postEntity2 = new PostEntity()
					.content(postUpdate.getContent())
					.title(postUpdate.getTitle())
					.category(categoryEntity2)
					.user(userEntity1);
		
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));

		post = postServiceImpl.update(postEntity1.getId(), postUpdate, null, userDetailsImpl);
		
		assertNull(post.getFiles());
		assertEquals(post.getTitle(), postUpdate.getTitle());
		assertEquals(post.getContent(), postUpdate.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트(파일 O)를 수정하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPostWithFiles() {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		postEntity2 = new PostEntity()
					.content(postUpdate.getContent())
					.title(postUpdate.getTitle())
					.category(categoryEntity2)
					.user(userEntity1);

		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);

		post = postServiceImpl.update(postEntity1.getId(), postUpdate, uploadFiles, userDetailsImpl);
		
		assertNotNull(post.getFiles());
		assertEquals(post.getTitle(), postUpdate.getTitle());
		assertEquals(post.getContent(), postUpdate.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			postServiceImpl.update(postEntity1.getId(), postUpdate, null, userDetailsImpl);			
		});
		
		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(0)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 포스트를 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenThrowCategoryNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.empty());
		
		assertThrows(CategoryNotFoundException.class, () -> {
			postServiceImpl.update(postEntity1.getId(), postUpdate, null, userDetailsImpl);			
		});

		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트를 생성한 사용자와 일치하지 않아 수정하는데 실패한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenUserNotMatchedException() {
		UserEntity userEntity2;
		
		userEntity2 = new UserEntity()
				.email("user2@user2.com")	
				.name("사용자2")
				.password("12345678Aa!@#$%")
				.userRole(UserRole.USER);
		userEntity2.setId(2L);
		
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));

		assertThrows(UserNotMatchedException.class, () -> {
			postServiceImpl.update(postEntity1.getId(), postUpdate, null, new UserDetailsImpl(userEntity2));			
		});

		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}	
}