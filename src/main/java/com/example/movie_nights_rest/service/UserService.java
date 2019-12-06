package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Iterable<UserResponseCommand> getAll() {
        return userRepository.findAll().stream().map(UserResponseCommand::new).collect(Collectors.toList());
    }

    public UserResponseCommand create(String username, String password, ArrayList<String> roles) {
        var user = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .roles(roles).build();

        return new UserResponseCommand(userRepository.save(user));
    }
}
