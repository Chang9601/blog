package com.whooa.blog.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
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
	
	@AfterEach
	public void tearDown() {
		userRepository.deleteAll();
	}
	
	@DisplayName("사용자를 생성하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenUserEntity_whenCallSaveForCreate_thenReturnUserEntity(UserEntity userEntity) {
		UserEntity savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity);
		
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
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void given_whenCallSaveForDelete_thenReturnNothing(UserEntity userEntity) {
		userEntity.setActive(false);
		
		userRepository.save(userEntity);
		
		assertFalse(userEntity.getActive());
	}
	
	@DisplayName("사용자를 존재하지 않아 삭제하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void given_whenCallSaveForDelete_thenThrowInvalidDataAccessApiUsageException(UserEntity userEntity) {
		userEntity.setActive(false);
		
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			userRepository.save(null);
		});
	}
		
	@DisplayName("사용자를 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenId_whenCallFindByIdAndActiveTrue_thenReturnUserEntity(UserEntity userEntity1) {		
		UserEntity foundUserEntity, savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity1);
		foundUserEntity = userRepository.findByIdAndActiveTrue(savedUserEntity.getId()).get();

		assertEquals(savedUserEntity.getEmail(), foundUserEntity.getEmail());			
	}
	
	@DisplayName("사용자를 조회하는데 실패한다.")
	@Test
	public void givenId_whenCallFindByIdAndActiveTrue_thenThrowNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findByIdAndActiveTrue(100L).get();
		});
	}
	
	@DisplayName("사용자를 조회(이메일)하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenEmail_whenCallFindByEmailAndActiveTrue_thenReturnUserEntity(UserEntity userEntity) {		
		UserEntity foundUserEntity, savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity);
		foundUserEntity = userRepository.findByEmailAndActiveTrue(savedUserEntity.getEmail()).get();

		assertEquals(savedUserEntity.getEmail(), foundUserEntity.getEmail());			
	}

	@DisplayName("사용자를 조회(이메일)하는데 실패한다.")
	@Test
	public void givenEmail_whenCallFindByEmailAndActiveTrue_thenThrowNoSuchElementException() {		
		assertThrows(NoSuchElementException.class, () -> {
			userRepository.findByEmailAndActiveTrue("test@test.com").get();
		});
	}
	
	@DisplayName("활성화된 사용자 목록을 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenPagination_whenCallFindByActiveTrue_thenReturnUserEntities(UserEntity userEntity1) {
		UserEntity userEntity2;
		Page<UserEntity> page;
		
		userEntity2 = userRepository.save(
			UserEntity.builder()
						.email("user2@naver.com")
						.name("사용자2")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build()
		);
		userEntity2.setActive(false);
		
		userRepository.save(userEntity1);
		userRepository.save(userEntity2);
		
		page = userRepository.findByActiveTrue(new PaginationUtil().makePageable());
		
		assertEquals(1, page.getTotalElements());			
	}

	@DisplayName("사용자가 할성 상태라서 조회하는데 성공한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenIdAndActive_whenCallFindByIdAndActiveTrue_thenReturnUserEntity(UserEntity userEntity) {		
		UserEntity foundUserEntity, savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity);
		foundUserEntity = userRepository.findByIdAndActiveTrue(savedUserEntity.getId()).get();

		assertEquals(savedUserEntity.getEmail(), foundUserEntity.getEmail());
	}
	
	@DisplayName("사용자가 비할성 상태라서 조회하는데 실패한다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenIdAndActive_whenCallFindByIdAndActiveTrue_thenReturnNull(UserEntity userEntity) {
		userEntity.setActive(false);
		UserEntity savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity);

		assertEquals(Optional.empty(), userRepository.findByIdAndActiveTrue(savedUserEntity.getId()));
	}	
	
	@DisplayName("사용자가 존재하다.")
	@ParameterizedTest
	@MethodSource("userParametersProvider")
	public void givenEmail_whenCallExistsByEmail_thenReturnTrue(UserEntity userEntity) {		
		boolean result;
		UserEntity savedUserEntity;
		
		savedUserEntity = userRepository.save(userEntity);
		result = userRepository.existsByEmail(savedUserEntity.getEmail());
		
		assertTrue(result);			
	}

	@DisplayName("사용자가 존재하지 않는다.")
	@Test
	public void givenEmail_whenCallExistsByEmail_thenReturnFalse() {
		boolean result = userRepository.existsByEmail("test@test.com");
		
		assertFalse(result);
	}
	
	private static Stream<Arguments> userParametersProvider() {
		return Stream.of(Arguments.of(
			UserEntity.builder()
						.email("user1@naver.com")
						.name("사용자1")
						.password("12345678Aa!@#$%")
						.userRole(UserRole.USER)
						.build()
		));
	}	
}