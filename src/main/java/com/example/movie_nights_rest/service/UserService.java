package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.exception.ResourceNotFoundException;
import com.example.movie_nights_rest.model.AuthProvider;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Iterable<UserResponseCommand> getAll() {
        return userRepository.findAll().stream().map(UserResponseCommand::new).collect(Collectors.toList());
    }

    public UserResponseCommand create(String email, String password, ArrayList<String> roles, AuthProvider authProvider) {
        var user = User.builder()
                .email(email)
                .provider(authProvider)
                .password(bCryptPasswordEncoder.encode(password))
                .roles(roles).build();

        return new UserResponseCommand(userRepository.save(user));
    }

    public UserResponseCommand getById(String id) {
        return userRepository.findById(id).map(UserResponseCommand::new).orElseThrow(ResourceNotFoundException::new);
    }
}
