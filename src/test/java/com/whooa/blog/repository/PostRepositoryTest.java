package com.whooa.blog.repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

import static org.junit.jupiter.api.Assertions.*;

/*
  @DataJpaTest 어노테이션은 테스트 목적으로 내장형 인 메모리 데이터베이스를 자동으로 구성하는 영속성 계층 구성 요소를 테스트할 때 사용된다.
  @DataJpaTest 어노테이션은 @Component, @Controller, @Service과 같은 Spring 빈을 ApplicationContext에 로드하지 않는다.
  기본적으로 @Entity 클래스를 스캔하고 @Repository 어노테이션이 지정된 Spring Data JPA 레포지토리를 구성한다.
  기본적으로 @DataJpaTest 어노테이션이 지정된 테스트는 트랜잭션 처리되며 각 테스트의 끝에서 롤백된다. 
*/
@DataJpaTest
/* 인 메모리 데이테베이스가 아니라 MySQL을 사용하려면 Replace.NONE으로 설정한다. */
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	
	private PostEntity postEntity;

	@BeforeEach
	public void setUp() {
		postEntity = new PostEntity("테스트", "테스트를 위한 포스트");
	}
	
	@DisplayName("포스트를 생성하는데 성공한다.")
	@Test
	public void givenPostEntity_whenCallSave_thenReturnPostEntity() {
		// given: 설정.
		
		// when: 행위.
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		// then: 검증.
		assertNotNull(savedPostEntity);
		assertTrue(savedPostEntity.getId() > 0);
	}

	@DisplayName("포스트를 작성하지 않아 포스트를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSave_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.save(null);
		});
	}
	
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenNothing_whenCallFindAll_thenReturnAllPostEntities() {
		PostEntity postEntity2 = new PostEntity("실전", "실전을 위한 포스트");
		
		postRepository.save(postEntity);
		postRepository.save(postEntity2);
		
		List<PostEntity> postEntities = postRepository.findAll();
		
		assertEquals(postEntities.size(), 2);			
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트 목록을 조회하는데 실패한다.")
	@Test
	public void givenNothing_whenCallFindAll_thenReturnNothing() {
		List<PostEntity> postEntities = postRepository.findAll();
		
		assertEquals(postEntities.size(), 0);
	}	
	
	@DisplayName("포스트를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindById_thenReturnPostEntity() {		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
				
		assertNotNull(foundPostEntity);
		assertEquals(foundPostEntity.getTitle(), savedPostEntity.getTitle());			
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {
		postRepository.save(postEntity);
		
		/*
		  둘 중 어느 방법?
		  Assertions.assertEquals(postRepository.findById(1000L), Optional.empty());
		*/
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(1000L).get();
		});
	}
	
	@DisplayName("포스트를 갱신하는데 성공한다.")
	@Test
	public void givenPostEntity_whenCallSave_thenReturnUpdatedPostEntity() {		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
		
		foundPostEntity.setTitle("실전");
		foundPostEntity.setContent("실전을 위한 포스트");
		
		PostEntity updatedPostEntity = postRepository.save(foundPostEntity);

		assertEquals(updatedPostEntity.getTitle(), foundPostEntity.getTitle());
		assertEquals(updatedPostEntity.getContent(), foundPostEntity.getContent());
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 갱신하는데 실패한다.")
	@Test
	public void givenNull_whenCallSave_thenInvalidDataAccessApiUsageException() {
		PostEntity savedPostEntity = postRepository.save(postEntity);
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
		
		foundPostEntity.setTitle("실전");
		foundPostEntity.setContent("실전을 위한 포스트");

		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.save(null);
		});
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenPostEntity_whenCallDelete_thenReturnNothing() {		
		PostEntity savedPostEntity = postRepository.save(postEntity);
		
		postRepository.delete(savedPostEntity);
		/*
		  Optional cannot be resolved to a type 오류.
		  Optional<PostEntity> postEntityOptional = postRepository.findById(savedPostEntity.getId());
		*/
		assertEquals(postRepository.findById(savedPostEntity.getId()), Optional.empty());
	}
	
	@DisplayName("포스트가 존재하지 않아서 포스트를 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {		
		postRepository.save(postEntity);
		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.delete(null);
		});
	}
}