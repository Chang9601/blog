package com.whooa.blog.repository;

import static org.junit.jupiter.api.Assertions.*;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.whooa.blog.category.entity.CategoryEntity;
import com.whooa.blog.category.repository.CategoryRepository;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.post.repository.PostRepository;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;


/* 
 * AuditingEntityListener 메서드는 @PrePersis와 @PreUpdate 상태에서 호출된다.
 * 즉, 삽입 혹은 수정 SQL 문장이 실행되기 전에 호출된다.
 * 어노테이션이 없으면 createdAt 필드와 updatedAt 필드에 null 값이 들어가서 오류가 발생한다.
 */
@EnableJpaAuditing
/*
 * @DataJpaTest 어노테이션은 테스트 목적으로 내장형 인 메모리 데이터베이스를 자동으로 구성하는 영속성 계층 구성 요소를 테스트할 때 사용된다.
 * @DataJpaTest 어노테이션은 @Component, @Controller, @Service과 같은 Spring 빈을 ApplicationContext에 로드하지 않는다.
 * @Entity 클래스를 스캔하고 @Repository 어노테이션이 지정된 Spring Data JPA 레포지토리를 구성한다.
 * @DataJpaTest 어노테이션이 지정된 테스트는 트랜잭션 처리되며 각 테스트의 끝에서 롤백된다. 
 */
@DataJpaTest
/* 인 메모리 데이테베이스가 아니라 MySQL을 사용하려면 Replace.NONE으로 설정한다. */
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;
	
	private PostEntity postEntity1;
	private CategoryEntity categoryEntity1;
	private UserEntity userEntity;
	
	private PaginationUtil pagination;
	private Pageable pageable;

	@BeforeEach
	public void setUp() {
		categoryEntity1 = categoryRepository.save(new CategoryEntity().name("테스트 카테고리"));
				
		userEntity = userRepository.save(new UserEntity()
				.email("test@test.com")
				.name("테스트 이름")
				.password("1234")
				.userRole(UserRole.USER));
		
		postEntity1 = new PostEntity()
				.content("테스트 내용")
				.title("테스트 제목")
				.category(categoryEntity1)
				.user(userEntity);
		
		pagination = new PaginationUtil();
		pageable = pagination.makePageable();
	}
	
	@DisplayName("포스트를 생성하는데 성공한다.")
	@Test
	public void givenPostEntity_whenCallSaveForCreate_thenReturnPostEntity() {
		/*
		 * given: 설정.
		 * when: 행위.
		 * then: 검증.
		 */
		PostEntity savedPostEntity = postRepository.save(postEntity1);
		
		assertTrue(savedPostEntity.getId() > 0);
	}

	@DisplayName("포스트를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForCreate_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.save(null);
		});
	}
	
	@DisplayName("포스트를 삭제하는데 성공한다.")
	@Test
	public void givenPostEntity_whenCallDelete_thenReturnNothing() {		
		PostEntity savedPostEntity = postRepository.save(postEntity1);
		
		postRepository.delete(savedPostEntity);
		/*
		 * Optional cannot be resolved to a type 오류.
		 * Optional<PostEntity> postEntity1Optional = postRepository.findById(savedPostEntity.getId());
		 */
		assertEquals(postRepository.findById(savedPostEntity.getId()), Optional.empty());
	}
	
	@DisplayName("포스트가 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void givenNull_whenCallDelete_thenThrowInvalidDataAccessApiUsageException() {		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.delete(null);
		});
	}
	
	@DisplayName("포스트를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindById_thenReturnPostEntity() {		
		PostEntity savedPostEntity = postRepository.save(postEntity1);
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();

		assertEquals(foundPostEntity.getTitle(), savedPostEntity.getTitle());			
	}
	
	@DisplayName("포스트를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindById_thenThrowNoSuchElementException() {		
		/*
		 * 둘 중 어느 방법?
		 * Assertions.assertEquals(postRepository.findById(1000L), Optional.empty());
		 */
		assertThrows(NoSuchElementException.class, () -> {
			postRepository.findById(1L).get();
		});
	}
		
	@DisplayName("포스트 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindAll_thenReturnPostEntities() {
		PostEntity postEntity2 = new PostEntity()
				.content("실전 내용")
				.title("실전 제목")
				.category(categoryEntity1)
				.user(userEntity);
		
		postRepository.save(postEntity1);
		postRepository.save(postEntity2);
		
		Page<PostEntity> page = postRepository.findAll(pageable);
		
		assertEquals(page.getTotalElements(), 2);	
	}

	@DisplayName("포스트 목록을 조회(카테고리 아이디)하는데 성공한다.")
	@Test
	public void givenPagination_whenCallfindByCategoryId_thenReturnPostEntitiesByCategoryId() {
		CategoryEntity categoryEntity2 = categoryRepository.save(new CategoryEntity().name("실전 카테고리"));

		PostEntity postEntity2 = new PostEntity()
				.content("실전 내용")
				.title("실전 제목")
				.category(categoryEntity2)
				.user(userEntity);
		
		postRepository.save(postEntity1);
		postRepository.save(postEntity2);
		
		Page<PostEntity> page = postRepository.findByCategoryId(categoryEntity2.getId(), pageable);
		
		assertEquals(page.getTotalElements(), 1);
	}

	@DisplayName("포스트를 수정하는데 성공한다.")
	@Test
	public void givenPostEntity_whenCallSaveForUpdate_thenReturnUpdatedPost() {		
		PostEntity savedPostEntity = postRepository.save(postEntity1);
		PostEntity foundPostEntity = postRepository.findById(savedPostEntity.getId()).get();
		
		foundPostEntity.content("실전 내용").title("실전 제목");

		PostEntity updatedPostEntity = postRepository.save(foundPostEntity);

		assertEquals(updatedPostEntity.getTitle(), foundPostEntity.getTitle());
		assertEquals(updatedPostEntity.getContent(), foundPostEntity.getContent());
	}
	
	@DisplayName("포스트가 존재하지 않아 수정하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForUpdate_thenThrowInvalidDataAccessApiUsageException() {		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			postRepository.save(null);
		});
	}
}