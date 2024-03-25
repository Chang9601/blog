package com.whooa.blog.repository;

import java.util.List;

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
	
	@DisplayName("포스트 레포지토리의 save() 메서드 테스트")
	@Test
	public void givenPostObject_whenSave_thenReturnSavedPost() {
		// given: 설정
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle("테스트");
		postEntity.setDescription("테스트를 위한 포스트");
		postEntity.setContent("테스트를 위한 포스트");
		
		// when: 테스트할 행위
		PostEntity savedPost = postRepository.save(postEntity);
		
		// then: 검증
		Assertions.assertNotNull(savedPost);
		Assertions.assertTrue(savedPost.getId() > 0);
	}
	
	@DisplayName("포스트 레포지토리의 findAll() 메서드 테스트")
	@Test
	public void givenPosts_whenFindAll_thenReturnAllPosts() {
		PostEntity postEntity1 = new PostEntity();
		postEntity1.setTitle("테스트1");
		postEntity1.setDescription("테스트1을 위한 포스트");
		postEntity1.setContent("테스트1을 위한 포스트");
		
		PostEntity postEntity2 = new PostEntity();
		postEntity2.setTitle("테스트2");
		postEntity2.setDescription("테스트2를 위한 포스트");
		postEntity2.setContent("테스트2를 위한 포스트");
		
		postRepository.save(postEntity1);
		postRepository.save(postEntity2);
		
		List<PostEntity> postEntities = postRepository.findAll();
		
		Assertions.assertNotNull(postEntities);
		Assertions.assertEquals(postEntities.size(), 2);			
	}
	
	@DisplayName("포스트 레포지토리의 findById() 메서드 테스트")
	@Test
	public void givenPost_whenFindById_thenReturnPost() {
		PostEntity postEntity = new PostEntity();
		postEntity.setTitle("테스트1");
		postEntity.setDescription("테스트를 위한 포스트");
		postEntity.setContent("테스트를 위한 포스트");
		
		PostEntity savedPost = postRepository.save(postEntity);
		
		PostEntity foundPostEntity = postRepository.findById(savedPost.getId()).get();
				
		Assertions.assertNotNull(foundPostEntity);
		Assertions.assertEquals(foundPostEntity.getTitle(), postEntity.getTitle());			
	}	
}
