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
                .roles(roles).build();

        return new UserResponseCommand(userRepository.save(user));
    }

    public UserResponseCommand getById(String id) {
        return userRepository.findById(id).map(UserResponseCommand::new).orElseThrow(ResourceNotFoundException::new);
    }

    public Iterable<UserResponseCommand> getUserFriends(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(ResourceNotFoundException::new);

        return user.getFriends().stream().map(UserResponseCommand::new).collect(Collectors.toList());
    }

    public void removeUserFromFriends(String principalId, String friendId) {
        var principal = userRepository.findById(principalId).orElseThrow(ResourceNotFoundException::new);
        var friend = principal.getFriends().stream()
                .filter(user -> user.getId().equals(friendId))
                .findFirst().orElseThrow(ResourceNotFoundException::new);

        principal.getFriends().remove(friend);
        var found = friend.getFriends().stream()
                .filter(user -> user.getId().equals(principalId)).findFirst();

        found.ifPresent(user -> friend.getFriends().remove(user));

        userRepository.save(principal);
        userRepository.save(friend);
    }
}
