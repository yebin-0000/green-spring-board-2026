package com.green.spring_board.scheduler;

import com.green.spring_board.repository.AccessTokenBlacklistRepository;
import com.green.spring_board.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // Bean을 사용할 수 있도록
@AllArgsConstructor // 생성자
@Slf4j // 로깅 -
public class TokenCleanupScheduler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Transactional
    // 초 분 시 일 월 요일
    @Scheduled(cron = "0 0 3 * * * *")
    public void cleanTokens() {
        LocalDateTime now = LocalDateTime.now();
        log.warn("토큰 정리 스케줄러가 실행됩니다. 실행 시작 시간: {}", now);

        // 1. RefreshToken 테이블에서 만료 시간이 지난 리프레시 토큰들 찾아서 제거
        refreshTokenRepository.deleteAllByExpirationDateTimeBefore(now);

        // 2. AccessTokenBlacklist 테이블에서 만료 시간이 지난 액세스 토큰들 찾아서 제거
        accessTokenBlacklistRepository.deleteAllByExpirationDateTimeBefore(now);
        log.warn("토큰 정리 스케줄러의 작업이 성공적으로 마무리 되었습니다");

    }
}
