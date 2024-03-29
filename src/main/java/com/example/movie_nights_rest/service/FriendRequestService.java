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

    public Iterable<FriendRequestCommand> getPendingFriendRequests(String id) {
        return friendRequestRepository.findAllByReceiverId(id)
                .stream().map(FriendRequestCommand::new).collect(Collectors.toList());
    }

    public Iterable<FriendRequestCommand> getCreatedFriendRequests(String id) {
        return friendRequestRepository.findAllBySenderId(id)
                .stream().map(FriendRequestCommand::new).collect(Collectors.toList());
    }

    public void createFriendRequest(String userId, String email) {
        var principal = userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new);

        if (principal.getEmail().equals(email))
            throw new BadRequestException("You cannot send friend request to yourself.");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User do not exist."));

        if (principal.getFriends().contains(user))
            throw new BadRequestException("You are already friend with this user.");

        if (!friendRequestRepository.findBySenderIdAndReceiverId(principal.getId(), user.getId()).isEmpty())
            throw new BadRequestException("You have already send friend request for this user");

        var friendRequest = FriendRequest.builder().sender(principal).receiver(user).build();

        friendRequestRepository.save(friendRequest);
    }

    public void acceptFriendRequest(String userId, String requestId) {
        var request = friendRequestRepository.findById(requestId).orElseThrow(ResourceNotFoundException::new);
        if (!request.getReceiver().getId().equals(userId)) throw new ResourceNotFoundException();

        friendRequestRepository.delete(request);

        var sender = request.getSender();
        var receiver = request.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userRepository.save(receiver);
        userRepository.save(sender);
    }

    public void discardFriendRequest(String userId, String requestId) {
        var request = friendRequestRepository.findById(requestId).orElseThrow(ResourceNotFoundException::new);

        if (!request.getReceiver().getId().equals(userId)
                && !request.getSender().getId().equals(userId))
            throw new ResourceNotFoundException();

        friendRequestRepository.delete(request);
    }
}
