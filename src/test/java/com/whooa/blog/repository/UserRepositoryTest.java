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

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.PaginationUtil;

@EnableJpaAuditing
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	
	private UserEntity userEntity1;
	
	private PaginationUtil pagination;
	private Pageable pageable;

	@BeforeEach
	public void setUp() {
		userEntity1 = new UserEntity()
					.email("user@user.com")
					.name("사용자")
					.password("12345678Aa!@#$%")
					.userRole(UserRole.USER);
		
		pagination = new PaginationUtil();
		pageable = pagination.makePageable();
	}
	
	@DisplayName("사용자를 생성하는데 성공한다.")
	@Test
	public void givenUserEntity_whenCallSaveForCreate_thenReturnUserEntity() {
		UserEntity savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);
		
		assertTrue(savedUserEntity.getId() > 0);
	}
	
	@DisplayName("사용자를 생성하는데 실패한다.")
	@Test
	public void givenNull_whenCallSaveForCreate_thenThrowInvalidDataAccessApiUsageException() {	
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			userRepository.save(null);
		});
	}
	
	@DisplayName("사용자를 삭제하는데 성공한다.")
	@Test
	public void given_whenCallSaveForDelete_thenReturnNothing() {
		userEntity1.active(false);
		
		userRepository.save(userEntity1);
		
		assertFalse(userEntity1.getActive());
	}
	
	@DisplayName("사용자를 존재하지 않아 삭제하는데 실패한다.")
	@Test
	public void given_whenCallSaveForDelete_thenThrowInvalidDataAccessApiUsageException() {
		userEntity1.active(false);
		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			userRepository.save(null);
		});
	}
		
	@DisplayName("사용자를 조회하는데 성공한다.")
	@Test
	public void givenId_whenCallFindByIdAndActiveTrue_thenReturnUserEntity() {		
		UserEntity foundUserEntity, savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);
		foundUserEntity = userRepository.findByIdAndActiveTrue(savedUserEntity.getId()).get();

		assertEquals(foundUserEntity.getEmail(), savedUserEntity.getEmail());			
	}
	
	@DisplayName("사용자를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindByIdAndActiveTrue_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findByIdAndActiveTrue(100L).get();
		});
	}
	
	@DisplayName("사용자를 조회(이메일)하는데 성공한다.")
	@Test
	public void givenEmail_whenCallFindByEmailAndActiveTrue_thenReturnUserEntity() {		
		UserEntity foundUserEntity, savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);
		foundUserEntity = userRepository.findByEmailAndActiveTrue(savedUserEntity.getEmail()).get();

		assertEquals(foundUserEntity.getEmail(), savedUserEntity.getEmail());			
	}

	@DisplayName("사용자를 조회(이메일)하는데 실패한다.")
	@Test
	public void givenEmail_whenCallFindByEmailAndActiveTrue_thenThrowNoSuchElementException() {		
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findByEmailAndActiveTrue("test@test.com").get();
		});
	}
	
	@DisplayName("활성화된 사용자 목록을 조회하는데 성공한다.")
	@Test
	public void givenPagination_whenCallFindByActiveTrue_thenReturnUserEntities() {
		UserEntity userEntity2;
		Page<UserEntity> page;
		
		userEntity2 = userRepository.save(new UserEntity()
					.active(false)
					.email("user2@user2.com")
					.name("사용자2")
					.password("1234")
					.userRole(UserRole.USER));
		
		userRepository.save(userEntity1);
		userRepository.save(userEntity2);
		
		page = userRepository.findByActiveTrue(pageable);
		
		assertEquals(page.getTotalElements(), 1);			
	}

	@DisplayName("사용자가 할성 상태라서 조회하는데 성공한다.")
	@Test
	public void givenIdAndActive_whenCallFindByIdAndActiveTrue_thenReturnUserEntity() {		
		UserEntity foundUserEntity, savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);
		foundUserEntity = userRepository.findByIdAndActiveTrue(savedUserEntity.getId()).get();

		assertEquals(foundUserEntity.getEmail(), savedUserEntity.getEmail());
	}
	
	@DisplayName("사용자가 비할성 상태라서 조회하는데 실패한다.")
	@Test
	public void givenIdAndActive_whenCallFindByIdAndActiveTrue_thenReturnNull() {
		userEntity1.active(false);
		UserEntity savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);

		assertEquals(userRepository.findByIdAndActiveTrue(savedUserEntity.getId()), Optional.empty());
	}	
	
	@DisplayName("사용자가 존재하다.")
	@Test
	public void givenEmail_whenCallExistsByEmail_thenReturnTrue() {		
		boolean result;
		UserEntity savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);
		result = userRepository.existsByEmail(savedUserEntity.getEmail());
		
		assertTrue(result);			
	}

	@DisplayName("사용자가 존재하지 않는다.")
	@Test
	public void givenEmail_whenCallExistsByEmail_thenReturnFalse() {
		boolean result = userRepository.existsByEmail("test@test.com");
		
		assertFalse(result);
	}
}