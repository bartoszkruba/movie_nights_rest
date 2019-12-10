package com.example.movie_nights_rest.repository;

import com.example.movie_nights_rest.model.FriendRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    List<FriendRequest> findAllBySender(String sender);

    List<FriendRequest> findAllByReceiver(String receiver);

    List<FriendRequest> findAllBySenderAndReceiver(String sender, String receiver);
}
