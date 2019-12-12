package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.annotation.CurrentUser;
import com.example.movie_nights_rest.command.friendRequest.FriendRequestCommand;
import com.example.movie_nights_rest.command.user.UserResponseCommand;
import com.example.movie_nights_rest.config.security.UserPrincipal;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.FriendRequestService;
import com.example.movie_nights_rest.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("/api/user")
@Api("Endpoints for users.")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final FriendRequestService friendRequestService;

    @GetMapping
    @Secured({Role.ADMIN})
    @ApiOperation("Get all users. Available for ADMIN users.")
    public Iterable<UserResponseCommand> getAll() {
        return userService.getAll();
    }


    @GetMapping("/me")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Get information about your account. Available for registered users.")
    public UserResponseCommand getCurrentUser(@ApiIgnore @CurrentUser UserPrincipal userPrincipal) {
        return userService.getById(userPrincipal.getId());
    }

    @GetMapping("/me/friend")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Get all your friends. Available for registered users.")
    public Iterable<UserResponseCommand> getUserFriends(@ApiIgnore @CurrentUser UserPrincipal userPrincipal) {
        return userService.getUserFriends(userPrincipal.getEmail());
    }

    @DeleteMapping("/me/friend/{id}")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Remove user from your friends. Available for registered users.")
    public void removeFriend(
            @ApiIgnore @CurrentUser UserPrincipal userPrincipal,
            @ApiParam("Friend ID") @PathVariable String id) {
        userService.removeUserFromFriends(userPrincipal.getId(), id);
    }

    @GetMapping("/{id}")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Get user by id. Available for registered users.")
    public UserResponseCommand getUserById(@ApiParam("User ID") @PathVariable String id) {
        return userService.getById(id);
    }

    @PostMapping("/friendRequest")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Create friend request for a user. Available for registered users.")
    @ResponseStatus(HttpStatus.CREATED)
    public void createFriendRequest(
            @ApiIgnore @CurrentUser UserPrincipal userPrincipal,
            @ApiParam("User email") @RequestParam String email) {

        friendRequestService.createFriendRequest(userPrincipal.getId(), email);
    }

    @PostMapping("/me/friendRequest/{id}")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Accept friend request. Available for registered users.")
    @ResponseStatus(HttpStatus.CREATED)
    public void acceptFriendRequest(
            @ApiIgnore @CurrentUser UserPrincipal userPrincipal,
            @ApiParam("Request id") @PathVariable String id
    ) {
        friendRequestService.acceptFriendRequest(userPrincipal.getId(), id);
    }

    @DeleteMapping("/me/friendRequest/{id}")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Discard friend request. Available for registered users.")
    public void discardFriendReqeust(
            @ApiIgnore @CurrentUser UserPrincipal userPrincipal,
            @ApiParam("Request id") @PathVariable String id) {
        friendRequestService.discardFriendRequest(userPrincipal.getId(), id);
    }

    @GetMapping("/me/pendingFriendRequest")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Get list of friend request waiting for acceptance. Available for registered users.")
    public Iterable<FriendRequestCommand> getPendingFriendRequests(@ApiIgnore @CurrentUser UserPrincipal userPrincipal) {
        return friendRequestService.getPendingFriendRequests(userPrincipal.getId());
    }

    @GetMapping("/me/createdFriendRequest")
    @Secured({Role.BASIC, Role.ADMIN})
    @ApiOperation("Get list of friend request created by you. Available for registered users.")
    public Iterable<FriendRequestCommand> getCreatedFriendRequests(@ApiIgnore @CurrentUser UserPrincipal userPrincipal) {
        return friendRequestService.getCreatedFriendRequests(userPrincipal.getId());
    }
}
