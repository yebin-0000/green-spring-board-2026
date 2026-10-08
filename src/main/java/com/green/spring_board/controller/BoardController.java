package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<Page<BoardResponse>>> getBoards(
            HttpServletRequest httpServletRequest,
            //지금 내가 몇 번째 페이지에 있는지, 한 번에 몇 개의 컨텐츠를 보여줄 건지
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String order
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int userId =(int) session.getAttribute("userId");

        return ResponseEntity.ok(
                ApiResponse.ok(boardService.getAllBoards(userId, page, size, order))
        );

    }

    //내 게시글 조회
    @GetMapping("/my-boards")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getMyBoards(
            //@PathVariable int Userid 경로가 노출됨
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        int userId = (int) session.getAttribute("userId");
        List<BoardResponse> response = boardService.getMyBoards(userId);

        return ResponseEntity.ok(
                ApiResponse.ok(response)
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoardDetail(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        int userId = -1;
        if (session != null && session.getAttribute("userId") != null) {
            userId = (int) session.getAttribute("userId");
        }

        BoardResponse board = boardService.getBoard(id, userId);
        return ResponseEntity.ok(
                ApiResponse.ok(board)
        );
    }

    //삽입
    // 돌려줄 값이 없을 때는 돌려줄 값이 없다는 것도 명시해줘야 함
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createBoard(
            @Valid @RequestBody BoardCreateRequest boardCreateRequest,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }

        // 2. 세션에서 현재 유저의 ID를 꺼냄
        int userId = (int) session.getAttribute("userId");
        int newBoardId = boardService.createBoard(boardCreateRequest, userId);
        URI location = URI.create("/api/board/" + newBoardId);
        return ResponseEntity.created(location).body(ApiResponse.ok());

    }

    //수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(
            @PathVariable int id,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        // 본인 확인
        // 1. 세션에 저장된 사용자 ID 가져오기
        int userId = (int) session.getAttribute("userId");
        boardService.updateBoard(id, boardUpdateRequest, userId);
        return ResponseEntity.ok(ApiResponse.ok());

    }


    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        //삭제 성공 시 응답 방법
        // 1. 200 + ApiResponse<Void>
        // 2. 204(No Content) + No Body
        int userId = (int) session.getAttribute("userId");
        boardService.deleteBoard(id, userId);
        return ResponseEntity.ok(ApiResponse.ok());

    }

    //좋아요
    @PostMapping("/like/{id}")
    public ResponseEntity<ApiResponse<Void>> likeBoard(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        // 본인 확인
        // 1. 세션에 저장된 사용자 ID 가져오기
        int userId = (int) session.getAttribute("userId");

        boardService.pressLike(id, userId);
        return ResponseEntity.ok(ApiResponse.ok());

        //내가 이 게시글에 좋아요를 눌렀는지
    }
    //상세 눌렀을 때 좋아요 누른 유저들 나타내기
    @GetMapping("/like/{id}")
    public ResponseEntity<ApiResponse<LikeDetailResponse>> viewLikeDetails(
            @PathVariable int id,
            HttpServletRequest httpServletRequest
    ) {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다");
        }
        //이 게시글에 좋아요 누른 유저들의 유저명
        LikeDetailResponse response = boardService.getLikeDetail(id);
        return  ResponseEntity.ok(ApiResponse.ok(response));
    }
}
