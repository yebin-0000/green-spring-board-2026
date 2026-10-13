package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "그린 커뮤니티 회원 API", description = "회원 관엽 API모음입니다")
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BoardService boardService;

    @Operation(summary = "회원가입 API", description = "회원가입 할 때 씀")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {

            userService.signup(signupRequest);
            return ResponseEntity.ok(ApiResponse.ok());

    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        //DTO Valid: 입력값에 대한 검증 기능

            LoginResponse result = userService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.ok(result));


    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getCurrentUser(HttpServletRequest httpServletRequest) {
        // "내" 정보 조회하기(이메일, 닉네임)
        // 요청자의 정보 식별 후 내려주기
        //1. 이 사람의 세션을 가져옴
        /*me는 회원 전용 서비스. 세션이 없으면 새로 만들어 주는게 아니라 내쫓아야 함.
        세션이 없다고 세션을 만들지 않도록 getSession 안에 (false) 옵션을 추가한다*/

        MyInfoResponse response = userService.getUserInfo();

        return ResponseEntity.ok(ApiResponse.ok());
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(

    ) {
        return ResponseEntity.ok(ApiResponse.ok());

    }
    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        //이메일, 닉네임 업데이트 할 수 있도록
        //현재 유저를 가져와서, 해당 유저 정보를
        //사용자가 올린 요청으로 덮어씌운다
        //보드 했던 것처럼 null이면 수정하지 않기

        // 3. 서비스로 유저 ID와 수정할 데이터 전달
        userService.updateUserInfo(userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.ok());


    }
    //유저 탈퇴 기능
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            HttpServletRequest request
    ) {

        userService.deleteUser();
        //boardService.deleteBoard(id, userId);

        return  ResponseEntity.ok(ApiResponse.ok());
    }

    }


