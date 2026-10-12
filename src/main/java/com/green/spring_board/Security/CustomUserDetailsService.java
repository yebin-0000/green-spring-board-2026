package com.green.spring_board.Security;

import com.green.spring_board.entity.User;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

// 토큰에  적힌 이름을 가지고 장부(DB)를 뒤져 실제 살아있는 회원인지 대조
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 유저 네임을 전달 받아 DB내에 존재하는지 확인.
        User user = userRepository.findByUsername(username);

        //
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        if (user.getState() !=UserState.ACTIVE) {
            throw new UsernameNotFoundException("사용할 수 없는 계정입니다");
        }
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                //.authorities(user.getRole().toString())
                .build();
    }
}
