package com.whooa.blog.repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;

// @DataJpaTest 어노테이션은 테스트 목적으로 내장형 인 메모리 데이터베이스를 자동으로 구성하는 영속성 계층 구성 요소를 테스트할 때 사용된다.
// @DataJpaTest 어노테이션은 @Component, @Controller, @Service과 같은 Spring 빈을 ApplicationContext에 로드하지 않는다.
// 기본적으로 @Entity 클래스를 스캔하고 @Repository 어노테이션이 지정된 Spring Data JPA 레포지토리를 구성한다.
// 기본적으로 @DataJpaTest 어노테이션이 지정된 테스트는 트랜잭션 처리되며 각 테스트의 끝에서 롤백된다.
@DataJpaTest
// 인 메모리 데이테베이스가 아니라 MySQL을 사용하려면 Replace.NONE으로 설정한다.
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PostRepositoryTest {
	
	@Autowired
	private PostRepository postRepository;
	
	private PostEntity postEntity;

	@BeforeEach
	public void setUp() {
		String title = "테스트";
		String description = "테스트 포스트";
		String content = "테스트를 위한 포스트";
		
		postEntity = new PostEntity(title, description, content);
	}
	
	@DisplayName("포스트 레포지토리의 save() 메서드로 포스트 생성 성공 테스트")
	@Test
	public void givenPost_whenSave_thenReturnOneSavedPost() {
		// given: 설정.
		
		// when: 행위.
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		// then: 검증.
		Assertions.assertNotNull(savedPostEntity);
		Assertions.assertTrue(savedPostEntity.getId() > 0);
	}

	@DisplayName("포스트 레포지토리의 save() 메서드로 포스트 생성 실패 테스트")
	@Test
	public void givenPost_whenSave_thenThrowInvalidDataAccessApiUsageException() {	
		Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.save(null);
		});
	}
	
	@DisplayName("포스트 레포지토리의 findAll() 메서드로 포스트 목록 조회 성공 테스트")
	@Test
	public void givenPosts_whenFindAll_thenReturnAllFoundPosts() {
		PostEntity anotherPostEntity = new PostEntity("테스트2", "테스트2 포스트", "테스트2를 위한 포스트");
		
		postRepository.save(postEntity);
		postRepository.save(anotherPostEntity);
		
		List<PostEntity> foundPostEntities = postRepository.findAll();
		
		Assertions.assertEquals(foundPostEntities.size(), 2);			
	}
	
	@DisplayName("포스트 레포지토리의 findAll() 메서드로 포스트 목록 조회 실패 테스트")
	@Test
	public void givenPosts_whenFindAll_thenReturnNothing() {
		List<PostEntity> foundPostEntities = postRepository.findAll();
		
		Assertions.assertEquals(foundPostEntities.size(), 0);
	}	
	
	@DisplayName("포스트 레포지토리의 findById() 메서드로 포스트 조회 성공 테스트")
	@Test
	public void givenPost_whenFindById_thenReturnOneFoundPost() {		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
				
		Assertions.assertNotNull(foundPostEntity);
		Assertions.assertEquals(foundPostEntity.getTitle(), postEntity.getTitle());			
	}
	
	@DisplayName("포스트 레포지토리의 findById() 메서드로 포스트 조회 실패 테스트")
	@Test
	public void givenPost_whenFindById_thenThrowNoSuchElementException() {
		postRepository.save(postEntity);
		
		// 둘 중 어느 방법?
		// Assertions.assertEquals(postRepository.findById(1000L), Optional.empty());
		
		Assertions.assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(1000L).get();
		});
	}
	
	@DisplayName("포스트 레포지토리의 save() 메서드로 포스트 갱신 성공 테스트")
	@Test
	public void givenPost_whenSave_thenReturnOneUpdatedPost() {		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
		foundPostEntity.setTitle("실전");
		foundPostEntity.setDescription("실전 포스트");
		foundPostEntity.setContent("실전을 위한 포스트");
		
		PostEntity updatedPostEntity = postRepository.save(foundPostEntity);

		Assertions.assertEquals(updatedPostEntity.getTitle(), "실전");
		Assertions.assertEquals(updatedPostEntity.getDescription(), "실전 포스트");
		Assertions.assertEquals(updatedPostEntity.getContent(), "실전을 위한 포스트");
	}
	
	@DisplayName("포스트 레포지토리의 save() 메서드로 포스트 갱신 실패 테스트")
	@Test
	public void givenPost_whenSave_thenInvalidDataAccessApiUsageException() {
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
		foundPostEntity.setTitle("실전");
		foundPostEntity.setDescription("실전 포스트");
		foundPostEntity.setContent("실전을 위한 포스트");

		Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.save(null);
		});
	}
	
	@DisplayName("포스트 레포지토리의 delete() 메서드로 포스트 삭제 성공 테스트")
	@Test
	public void givenPost_whenDelete_thenReturnNothing() {		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		postRepository.delete(savedPostEntity);
		// Optional cannot be resolved to a type 오류.
		// Optional<PostEntity> postEntityOptional = postRepository.findById(savedPostEntity.getId());

		Assertions.assertEquals(postRepository.findById(savedPostEntity.getId()), Optional.empty());
	}
	
	@DisplayName("포스트 레포지토리의 delete() 메서드로 포스트 삭제 실패 테스트")
	@Test
	public void givenPost_whenDelete_thenThrowInvalidDataAccessApiUsageException() {		
		postRepository.save(postEntity);
		
		Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.delete(null);
		});
	}
}