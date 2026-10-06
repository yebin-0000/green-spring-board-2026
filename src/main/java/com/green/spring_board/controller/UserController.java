package com.green.spring_board.controller;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {

            userService.signup(signupRequest);
            return ResponseEntity.ok().build();

    }
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest) {
        //DTO Valid: 입력값에 대한 검증 기능

            int userId = userService.login(loginRequest);
            //세션 작업
            HttpSession session = httpServletRequest.getSession();//장부 정보 새로 만들기
            httpServletRequest.changeSessionId();
            session.setAttribute("userId", userId); //원하는 정보 새로 만들기(추가)
            return ResponseEntity.ok().build();


    }
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> getCurrentUser(HttpServletRequest httpServletRequest) {
        // "내" 정보 조회하기(이메일, 닉네임)
        // 요청자의 정보 식별 후 내려주기
        //1. 이 사람의 세션을 가져옴
        /*me는 회원 전용 서비스. 세션이 없으면 새로 만들어 주는게 아니라 내쫓아야 함.
        세션이 없다고 세션을 만들지 않도록 getSession 안에 (false) 옵션을 추가한다*/
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            //세션이 없음-로그인 한 적 없음.
            return ResponseEntity.status(401).build();
        }
        //2. 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        MyInfoResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok().body(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new  UnauthenticatedException("로그인이 필요합니다");
        }
        session.invalidate();
        return ResponseEntity.ok().build();

    }
    @PatchMapping
    public ResponseEntity<Void> updateUserInfo(
            HttpServletRequest request,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        //이메일, 닉네임 업데이트 할 수 있도록
        //현재 유저를 가져와서, 해당 유저 정보를
        //사용자가 올린 요청으로 덮어씌운다
        //보드 했던 것처럼 null이면 수정하지 않기

        // 1. 세션 체크(로그인 안 했으면 401에러)
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            throw new  UnauthenticatedException("로그인이 필요합니다");
        }

        // 2. 세션에서 현재 유저의 ID를 꺼냄
        int userId = (int) session.getAttribute("userId");

            // 3. 서비스로 유저 ID와 수정할 데이터 전달
            userService.updateUserInfo(userId, userUpdateRequest);
            return ResponseEntity.ok().build();


    }
    //유저 탈퇴 기능
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new  UnauthenticatedException("로그인이 필요합니다");
        }
        int userId = (int) session.getAttribute("userId");

        //1. DB삭제
        userService.deleteUser(userId);
        //2. 세션 비활성화
        session.invalidate();

        return  ResponseEntity.noContent().build();
    }

    }


