package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.nio.charset.StandardCharsets;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.admin.service.impl.AdminPostServiceImpl;
import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.exception.CategoryNotFoundException;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.file.value.File;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.exception.PostNotFoundException;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminPostServiceTest {
	@Mock
	private PostRepository postRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock	
	private FileService fileService;
	
	@InjectMocks
	private AdminPostServiceImpl adminPostServiceImpl;

	private PostUpdateRequest postUpdate;
	private PostResponse post;

	private File file;
    private MockMultipartFile[] uploadFiles;
    
	@BeforeAll
	public void setUp() {
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

	@DisplayName("포스트를 삭제하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("postParametersProvider")
	public void givenId_whenCallDelete_thenReturnNothing(PostEntity postEntity) {
		willDoNothing().given(postRepository).delete(any(PostEntity.class));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		
		adminPostServiceImpl.delete(postEntity.getId());

		then(postRepository).should(times(1)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("postParametersProvider")
	public void givenId_whenCallDelete_thenThrowPostNotFoundException(PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			adminPostServiceImpl.delete(postEntity.getId());
		});

		then(postRepository).should(times(0)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("postParametersProvider")
	public void givenPostUpdate_whenCallUpdate_thenReturnPost(PostEntity postEntity1, CategoryEntity categoryEntity1, UserEntity userEntity) {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2= new CategoryEntity().name("카테고리2");
		
		postEntity2 = new PostEntity()
					.content(postUpdate.getContent())
					.title(postUpdate.getTitle())
					.category(categoryEntity2)
					.user(userEntity);
			
		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));

		post = adminPostServiceImpl.update(postEntity1.getId(), postUpdate, null);
		
		assertNull(post.getFiles());
		assertEquals(postUpdate.getTitle(), post.getTitle());
		assertEquals(postUpdate.getContent(), post.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트(파일 O)를 수정하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("postParametersProvider")
	public void givenPostUpdate_whenCallUpdate_thenReturnPostWithFiles(PostEntity postEntity1, CategoryEntity categoryEntity1, UserEntity userEntity) {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = new CategoryEntity().name("카테고리2");
		
		postEntity2 = new PostEntity()
					.content(postUpdate.getContent())
					.title(postUpdate.getTitle())
					.category(categoryEntity2)
					.user(userEntity);

		given(postRepository.save(any(PostEntity.class))).willReturn(postEntity2);
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.of(categoryEntity1));
		given(fileService.upload(any(PostEntity.class), any(MultipartFile.class))).willReturn(file);

		post = adminPostServiceImpl.update(postEntity1.getId(), postUpdate, uploadFiles);
		
		assertNotNull(post.getFiles());
		assertEquals(postUpdate.getTitle(), post.getTitle());
		assertEquals(postUpdate.getContent(), post.getContent());

		then(postRepository).should(times(1)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(1)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("postParametersProvider")
	public void givenPostUpdate_whenCallUpdate_thenThrowPostNotFoundException(PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			adminPostServiceImpl.update(postEntity.getId(), postUpdate, null);			
		});
		
		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(0)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	@DisplayName("카테고리가 존재하지 않아 포스트를 수정하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("postParametersProvider")
	public void givenPostUpdate_whenCallUpdate_thenThrowCategoryNotFoundException(PostEntity postEntity) {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity));
		given(categoryRepository.findByName(any(String.class))).willReturn(Optional.empty());
		
		assertThrows(CategoryNotFoundException.class, () -> {
			adminPostServiceImpl.update(postEntity.getId(), postUpdate, null);			
		});

		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
	
	private static Stream<Arguments> postParametersProvider() {
		CategoryEntity categoryEntity = new CategoryEntity().name("카테고리1");
		
		UserEntity userEntity = new UserEntity()
								.email("user1@naver.com")
								.name("사용자1")
								.password("12345678Aa!@#$%")
								.userRole(UserRole.USER);
		userEntity.setId(1L);
			
		PostEntity postEntity = new PostEntity()
								.content("포스트1")
								.title("포스트1")
								.category(categoryEntity)
								.user(userEntity);

		return Stream.of(Arguments.of(postEntity, categoryEntity, userEntity));
	}		
}