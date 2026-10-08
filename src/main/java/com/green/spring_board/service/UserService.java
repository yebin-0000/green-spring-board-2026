package com.green.spring_board.service;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.dto.UserUpdateRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void signup(SignupRequest signupRequest) {
        //회원가입 로직 만들기

        // 이메일이 사용 중인지 확인
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceConflictException("Email already exists");
        }
        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword()
        );

        //DB save
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setNickname(signupRequest.getNickname());
        user.setState(UserState.ACTIVE);
        userRepository.save(user);
    }
    public int login(LoginRequest loginRequest) {
        //1. 이메일이 존재하는건지 확인
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        //이 이메일의 사용자 정보
        if (user.getState() == UserState.QUITTED) {
            throw new ResourceNotFoundException("탈퇴된 회원입니다");
        }

        //2. 비밀번호가 올바른지 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthenticatedException("Wrong password");
        }
        //3. 로그인 성공
        return user.getId();
    }
    //3. 유저 아이디로 DB 조회함
    public MyInfoResponse getUserInfo(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        if (user.getState() == UserState.QUITTED) {
            throw new ResourceNotFoundException("탈퇴된 회원입니다");
        }

        //4. DB에서 이 유저의 닉네임과 이메일을 받아옴
        String email = user.getEmail();
        String nickname = user.getNickname();

        //5. 돌려줌
        MyInfoResponse myInfoResponse = new MyInfoResponse();
        myInfoResponse.setEmail(email);
        myInfoResponse.setNickname(nickname);

        return myInfoResponse;

    }

    @Transactional
    public void updateUserInfo(int userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getState() == UserState.QUITTED) {
            throw new ResourceNotFoundException("탈퇴된 회원입니다");
        }

        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isBlank()) {
            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getNickname() != null && !userUpdateRequest.getNickname().isBlank()) {
            user.setNickname(userUpdateRequest.getNickname());
        }
    }

    public void deleteUser(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        //1. 상자에서 유저 객체를 꺼냄
        User user = userOptional.get();

        //2. DB에서 해당 유저 정보를 삭제함

        user.setState(UserState.QUITTED);
        userRepository.save(user);
    }
}
