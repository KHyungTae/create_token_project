package com.springboot.my_web_project.common.config.batch;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.my_web_project.repository.RefreshTokenRepository;

@Component
public class TokenCleanupScheduler {
	
	private final RefreshTokenRepository refreshTokenRepository;
	
	public TokenCleanupScheduler(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}
	
	//매일 새벽 3시에 만료된refreshToken 데이터 전체 삭제
	@Scheduled(cron = "0 0 3 * * ?")
	@Transactional
	public void cleanupExpiredTokens() {
		refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
	}
}
