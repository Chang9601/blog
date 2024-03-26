package com.whooa.blog.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
	
	@DisplayName("포스트 레포지토리의 save() 메서드로 포스트 생성 성공 테스트")
	@Test
	public void givenPost_whenSavePost_thenReturnSavedPost() {
		// given: 설정
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle("테스트");
		postEntity.setDescription("테스트 포스트");
		postEntity.setContent("테스트를 위한 포스트");
		
		// when: 행위
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		// then: 검증
		Assertions.assertNotNull(savedPostEntity);
		Assertions.assertTrue(savedPostEntity.getId() > 0);
	}
	
	@DisplayName("포스트 레포지토리의 findAll() 메서드로 포스트 목록 조회 성공 테스트")
	@Test
	public void givenPosts_whenFindAllPosts_thenReturnAllPosts() {
		PostEntity postEntity1 = new PostEntity();
		postEntity1.setTitle("테스트1");
		postEntity1.setDescription("테스트1 포스트");
		postEntity1.setContent("테스트1을 위한 포스트");
		
		PostEntity postEntity2 = new PostEntity();
		postEntity2.setTitle("테스트2");
		postEntity2.setDescription("테스트2 포스트");
		postEntity2.setContent("테스트2를 위한 포스트");
		
		postRepository.save(postEntity1);
		postRepository.save(postEntity2);
		
		List<PostEntity> foundPostEntities = postRepository.findAll();
		
		Assertions.assertNotNull(foundPostEntities);
		Assertions.assertEquals(foundPostEntities.size(), 2);			
	}
	
	@DisplayName("포스트 레포지토리의 findById() 메서드로 포스트 조회 성공 테스트")
	@Test
	public void givenPost_whenFindPostById_thenReturnPost() {
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle("테스트");
		postEntity.setDescription("테스트 포스트");
		postEntity.setContent("테스트를 위한 포스트");
		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
				
		Assertions.assertNotNull(foundPostEntity);
		Assertions.assertEquals(foundPostEntity.getTitle(), postEntity.getTitle());			
	}	

	@DisplayName("포스트 레포지토리의 save() 메서드로 포스트 갱신 성공 테스트")
	@Test
	public void givenPost_whenUpdatePost_thenReturnUpdatedPost() {
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle("테스트");
		postEntity.setDescription("테스트 포스트");
		postEntity.setContent("테스트를 위한 포스트");
		
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
	
	@DisplayName("포스트 레포지토리의 delete() 메서드로 포스트 삭제 성공 테스트")
	@Test
	public void givenPost_whenDeletePost_thenReturnEmpty() {
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle("테스트");
		postEntity.setDescription("테스트 포스트");
		postEntity.setContent("테스트를 위한 포스트");
		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		postRepository.delete(savedPostEntity);
		// Optional cannot be resolved to a type 오류.
		// Optional<PostEntity> postEntityOptional = postRepository.findById(savedPostEntity.getId());

		Assertions.assertEquals(postRepository.findById(savedPostEntity.getId()), Optional.empty());
	}		
}