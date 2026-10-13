package com.green.spring_board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
    // 기존 리프레시 토큰
    @NotBlank
    private String refreshToken;
}
