package com.green.spring_board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshResponse {
    // 새로운 액세스 토큰
    private String accessToken;
}
