package com.green.spring_board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeDetailResponse {
    //좋아요 누른 유저들의 이름 리스트
    private List<String> LikedUsernames;
}
