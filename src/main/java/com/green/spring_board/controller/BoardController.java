package com.green.spring_board.controller;

import com.green.spring_board.dto.BoardResponse;
import com.green.spring_board.dto.BoardUpdateRequest;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.dto.BoardCreateRequest;
import com.green.spring_board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

//try,catch문 전역 핸들러 적용

@RestController
@RequestMapping("/api/board")
@AllArgsConstructor
public class BoardController {
    private final BoardService boardService;


    //전체 조회
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards() {
        return ResponseEntity.ok(
                boardService.getAllBoards()
        );

    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardsDetail(@PathVariable int id) {

            BoardResponse board = boardService.getBoard(id);
            if (board == null) {
                return ResponseEntity.notFound().build();
        }
            return ResponseEntity.ok(board);

    }

    //삽입
    // 돌려줄 값이 없을 때는 돌려줄 값이 없다는 것도 명시해줘야 함
    @PostMapping
    public ResponseEntity<Void> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
            HttpServletRequest httpServletRequest
    ) {
            HttpSession session = httpServletRequest.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                throw new  UnauthenticatedException("로그인이 필요합니다");
            }

            // 2. 세션에서 현재 유저의 ID를 꺼냄
            int userId = (int) session.getAttribute("userId");
            int newBoardId = boardService.createBoard(boardCreateRequest, userId);
            URI location = URI.create("/api/board/" + newBoardId);
            return ResponseEntity.created(location).build();

    }

    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBoard(
            @Valid
            @PathVariable int id,
            @RequestBody BoardUpdateRequest boardUpdateRequest,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new  UnauthenticatedException("로그인이 필요합니다");
        }
        boardService.updateBoard(id, boardUpdateRequest);
        return ResponseEntity.ok().build();

    }


    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new  UnauthenticatedException("로그인이 필요합니다");
        }
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();

    }
}
