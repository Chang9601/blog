package com.whooa.blog.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.nio.charset.StandardCharsets;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
	@InjectMocks
	private AdminPostServiceImpl adminPostServiceImpl;
	
	@Mock
	private PostRepository postRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock	
	private FileService fileService;
	
	private PostEntity postEntity1;
	private CategoryEntity categoryEntity1;
	private UserEntity userEntity;
	
	private PostUpdateRequest postUpdate;
	private PostResponse post;

	private File file;
    private MockMultipartFile[] uploadFiles;
    
	@BeforeAll
	public void setUp() {
		postUpdate = new PostUpdateRequest();
		
		postUpdate.setCategoryName("카테고리2");
		postUpdate.setContent("포스트2");
		postUpdate.setTitle("포스트2");

		uploadFiles = new MockMultipartFile[] {
				new MockMultipartFile("test1", "test1.txt", "text/plain", "테스트 파일1".getBytes(StandardCharsets.UTF_8)),
				//new MockMultipartFile("test2", "test2.txt", "text/plain", "테스트 파일2".getBytes(StandardCharsets.UTF_8))
		};
		
		file = new File("txt", uploadFiles[0].getContentType(), uploadFiles[0].getOriginalFilename(), "D:\\spring-workspace\\whooa-blog\\upload\\test1.txt", uploadFiles[0].getSize());
		
		categoryEntity1 = new CategoryEntity();
		categoryEntity1.setName("카테고리1");

		userEntity = new UserEntity();
		userEntity.setEmail("user1@naver.com");
		userEntity.setName("사용자");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRole.USER);

		postEntity1 = new PostEntity();
		postEntity1.setContent("포스트1");
		postEntity1.setTitle("포스트1");
		postEntity1.setCategory(categoryEntity1);
		postEntity1.setUser(userEntity);
	}

	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenId_whenCallDelete_thenReturnNothing() {
		willDoNothing().given(postRepository).delete(any(PostEntity.class));
		given(postRepository.findById(any(Long.class))).willReturn(Optional.of(postEntity1));
		
		adminPostServiceImpl.delete(postEntity1.getId());

		then(postRepository).should(times(1)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenId_whenCallDelete_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());
		
		assertThrows(PostNotFoundException.class, () -> {
			adminPostServiceImpl.delete(postEntity1.getId());
		});

		then(postRepository).should(times(0)).delete(any(PostEntity.class));				
		then(postRepository).should(times(1)).findById(any(Long.class));
	}
	
	@DisplayName("포스트(파일 X)를 수정하는데 성공한다.")
	@Test
	public void givenPostUpdate_whenCallUpdate_thenReturnPost() {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = new CategoryEntity();
		categoryEntity2.setName("카테고리2");

		postEntity2 = new PostEntity();
		postEntity2.setContent(postUpdate.getContent());
		postEntity2.setTitle(postUpdate.getTitle());
		postEntity2.setCategory(categoryEntity2);
		postEntity2.setUser(userEntity);
		
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
	@Test
	@MethodSource("postParametersProvider")
	public void givenPostUpdate_whenCallUpdate_thenReturnPostWithFiles() {
		CategoryEntity categoryEntity2;
		PostEntity postEntity2;
		
		categoryEntity2 = new CategoryEntity();
		categoryEntity2.setName("카테고리2");

		postEntity2 = new PostEntity();
		postEntity2.setContent(postUpdate.getContent());
		postEntity2.setTitle(postUpdate.getTitle());
		postEntity2.setCategory(categoryEntity2);
		postEntity2.setUser(userEntity);

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
	@Test
	public void givenPostUpdate_whenCallUpdate_thenThrowPostNotFoundException() {
		given(postRepository.findById(any(Long.class))).willReturn(Optional.empty());

		assertThrows(PostNotFoundException.class, () -> {
			adminPostServiceImpl.update(postEntity1.getId(), postUpdate, null);			
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
			adminPostServiceImpl.update(postEntity1.getId(), postUpdate, null);			
		});

		then(postRepository).should(times(0)).save(any(PostEntity.class));
		then(postRepository).should(times(1)).findById(any(Long.class));
		then(categoryRepository).should(times(1)).findByName(any(String.class));
		then(fileService).should(times(0)).upload(any(PostEntity.class), any(MultipartFile.class));
	}
}