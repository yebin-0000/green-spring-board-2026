package com.green.spring_board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardResponse {
    int id; //Board ID
    String title; //제목
    String content; //내용
    int hits; //조회수
    Integer authorId; //작성자 ID
    String authorNickname; //작성자 닉네임
    LocalDateTime createdDatetime; //생성 일시
    LocalDateTime updatedDatetime; //수정 일시
}
