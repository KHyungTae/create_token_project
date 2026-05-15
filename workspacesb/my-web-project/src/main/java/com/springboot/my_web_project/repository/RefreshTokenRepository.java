package com.springboot.my_web_project.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.my_web_project.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	//특정 유저의 토큰 찾기 (토큰테이블에 userId는 기본키가아니라 따로 만들어야함)
	Optional<RefreshToken> findByUserId(String userId);
	
	//특정 토큰값이 존재하는지 확인
	//Optional<RefreshToken> findByTokenValue(String tokenValue);
	
	//매개변수로 보낸 시간보다 이전(Before)에 만료된 행들을 통째로 DELETE한다.
	void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
