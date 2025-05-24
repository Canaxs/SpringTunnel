package com.springtunnel.service;

import com.springtunnel.dto.UserDto;
import com.springtunnel.persistence.model.User;
import com.springtunnel.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

    public UserDto createUser(UserDto userDto) {
        try {
            userRepository.save(User.builder()
                            .username(userDto.getUsername())
                            .password(passwordEncoder.encode(userDto.getPassword()))
                            .build());
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
        return userDto;
    }
}
