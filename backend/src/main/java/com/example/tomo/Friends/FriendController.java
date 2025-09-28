package com.example.tomo.Friends;

import com.example.tomo.global.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController {

    private final FriendService friendService;
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<ResponseGetFriendsDto>>> getMyFriends(){
        try{
            return ResponseEntity.ok(ApiResponse.success(friendService.getFriends(),"성공"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("로그인된 사용자가 아닙니다"));
        }
    }

    @GetMapping("/friends/detail")
    public ResponseEntity<ApiResponse<List<ResponseFriendDetailDto>>> getFriendDetails(){
       try{
           return ResponseEntity.ok().body(ApiResponse.success(friendService.getDetailFriends(),"성공"));
       }catch(IllegalArgumentException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body(ApiResponse.failure("로그인된 사용자가 아닙니다"));
       }
    }

}
