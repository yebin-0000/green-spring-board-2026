package com.green.spring_board.controller;

import com.green.spring_board.dto.ApiResponse;
import com.green.spring_board.dto.CommentCreateRequest;
import com.green.spring_board.dto.CommentResponse;
import com.green.spring_board.dto.CommentUpdateRequest;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/board/{id}/comment")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable int id,
            @RequestBody CommentCreateRequest commentCreateRequest,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int userId =(int) session.getAttribute("userId");

        commentService.createComment(commentCreateRequest,userId,id);
        return ResponseEntity.ok(ApiResponse.ok());
    }
    // 댓글 조회-비회원도 가능
    @GetMapping("/board/{id}/comment")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> readComments(
            @PathVariable int id

    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(commentService.readComments(id))
        );

    }
    //댓글 수정
    @PatchMapping("/comment/{id}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable int id,
            @RequestBody CommentUpdateRequest commentUpdateRequest,
            HttpServletRequest httpServletRequest
    ){
        //코드 미완성
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        // 본인 확인
        // 1. 세션에 저장된 사용자 ID 가져오기
        int userId = (int) session.getAttribute("userId");

        commentService.updateComment(id, commentUpdateRequest, userId);

        return ResponseEntity.ok(ApiResponse.ok());
    }
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ) {//코드 미완성
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        // 본인 확인
        // 1. 세션에 저장된 사용자 ID 가져오기
        int userId = (int) session.getAttribute("userId");

        commentService.deleteComment(id, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok());
    }
}
