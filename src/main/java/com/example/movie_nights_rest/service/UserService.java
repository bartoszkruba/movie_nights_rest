package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Flux<UserResponseCommand> getAll() {
        return userRepository.findAll().map(UserResponseCommand::new);
    }

    public Mono<UserResponseCommand> create(String username, String password, ArrayList<String> roles) {
        var user = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .roles(roles).build();

        return userRepository.save(user).map(UserResponseCommand::new);
    }
}
