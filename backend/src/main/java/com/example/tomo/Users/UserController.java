package com.example.tomo.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/sign")
    public ResponseEntity<ResponseSignSuccessDto> signUser(@RequestBody RequestUserSignDto dto) {
        if(userService.validateUser(dto)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseSignSuccessDto(false, "User already exists"));
        }

        return ResponseEntity.ok(userService.signUser(dto));
    }



}
