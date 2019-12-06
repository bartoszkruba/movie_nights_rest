package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.auth.LoginRequest;
import com.example.movie_nights_rest.command.auth.LoginResponse;
import com.example.movie_nights_rest.repository.UserRepository;
import com.example.movie_nights_rest.util.JwtTokenUtil;
import com.example.movie_nights_rest.exception.UnauthorizedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public LoginResponse login(LoginRequest request) {

        var user = userRepository.findByUsername(request.getUsername().toLowerCase()).orElseThrow(UnauthorizedException::new);

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new UnauthorizedException();

        return new LoginResponse(jwtTokenUtil.generateToken(user));
    }
}
