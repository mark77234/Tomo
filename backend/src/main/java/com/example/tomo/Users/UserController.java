package com.example.tomo.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/friends")
    public String addFriendsUsingPhoneNumber(@RequestBody addFriendRequestDto dto) {

        return userService.addFriends(dto);
    }



}
