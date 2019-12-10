package com.example.movie_nights_rest.command.friendRequest;

import com.example.movie_nights_rest.model.FriendRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("Friend request response")
public class FriendRequestCommand {

    public FriendRequestCommand(FriendRequest friendRequest) {
        this.senderId = friendRequest.getSender().getId();
        this.senderEmail = friendRequest.getSender().getEmail();
        this.senderName = friendRequest.getSender().getName();

        this.receiverId = friendRequest.getReceiver().getId();
        this.receiverEmail = friendRequest.getReceiver().getId();
        this.receiverName = friendRequest.getReceiver().getName();
    }

    @ApiModelProperty("Sender ID")
    private String senderId;

    @ApiModelProperty("Sender email")
    private String senderEmail;

    @ApiModelProperty("Sender name")
    private String senderName;

    @ApiModelProperty("Receiver ID")
    private String receiverId;

    @ApiModelProperty("Receiver email")
    private String receiverEmail;

    @ApiModelProperty("Receiver name")
    private String receiverName;
}
