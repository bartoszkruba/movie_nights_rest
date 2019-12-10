package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.friendRequest.FriendRequestCommand;
import com.example.movie_nights_rest.exception.BadRequestException;
import com.example.movie_nights_rest.exception.ResourceNotFoundException;
import com.example.movie_nights_rest.exception.UnauthorizedException;
import com.example.movie_nights_rest.model.FriendRequest;
import com.example.movie_nights_rest.repository.FriendRequestRepository;
import com.example.movie_nights_rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public Iterable<FriendRequestCommand> getPendingFriendRequests(String email) {
        return friendRequestRepository.findAllByReceiver(email)
                .stream().map(FriendRequestCommand::new).collect(Collectors.toList());
    }

    public Iterable<FriendRequestCommand> getCreatedFriendRequests(String email) {
        return friendRequestRepository.findAllBySender(email)
                .stream().map(FriendRequestCommand::new).collect(Collectors.toList());
    }

    public void createFriendRequest(String userId, String email) {
        var principal = userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);

        var user = userRepository.findByEmail(email).orElseThrow(ResourceNotFoundException::new);

        if (principal.getFriends().contains(user)) throw new BadRequestException();

        if (!friendRequestRepository.findAllBySenderAndReceiver(principal.getId(), user.getId()).isEmpty())
            throw new BadRequestException();

        var friendRequest = FriendRequest.builder().sender(principal.getId()).receiver(user.getId()).build();

        friendRequestRepository.save(friendRequest);
    }

    public void acceptFriendRequest(String userId, String requestId) {
        var request = friendRequestRepository.findById(requestId).orElseThrow(ResourceNotFoundException::new);
        if (!request.getReceiver().equals(userId)) throw new UnauthorizedException();

        friendRequestRepository.delete(request);

        var sender = userRepository.findById(request.getSender()).orElseThrow(ResourceNotFoundException::new);
        var receiver = userRepository.findById(request.getReceiver()).orElseThrow(ResourceNotFoundException::new);

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userRepository.save(receiver);
        userRepository.save(sender);
    }

    public void discardFriendRequest(String userId, String requestId) {
        var request = friendRequestRepository.findById(requestId).orElseThrow(ResourceNotFoundException::new);

        if (!request.getReceiver().equals(userId) && !request.getSender().equals(userId))
            throw new UnauthorizedException();

        friendRequestRepository.delete(request);
    }
}
