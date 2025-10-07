package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Friend API", description = "친구 관련 API")
@RestController
@RequestMapping("/public")
public class FriendController {

    private final FriendService friendService;
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }



    @Operation(summary = "친구 목록 조회", description = "사용자의 친구 목록을 반환합니다")
    @GetMapping("/friends/list")
    public ResponseEntity<ApiResponse<List<ResponseFriendDetailDto>>> getFriendDetails() {
        try {
            return ResponseEntity.ok().body(ApiResponse.success(friendService.getDetailFriends(), "성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("로그인된 사용자가 아닙니다"));
        }
    }

}
