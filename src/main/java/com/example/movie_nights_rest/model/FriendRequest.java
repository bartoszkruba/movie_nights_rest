package com.example.movie_nights_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FriendRequest {
    @Id
    String id;

    // sender id
    String sender;

    // receiver id
    String receiver;
}
