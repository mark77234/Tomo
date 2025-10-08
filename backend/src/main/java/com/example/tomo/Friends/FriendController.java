package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<List<ResponseFriendDetailDto>>> getFriendDetails(@AuthenticationPrincipal String Uid) {
        try {
            return ResponseEntity.ok().body(ApiResponse.success(friendService.getDetailFriends(Uid), "성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("로그인된 사용자가 아닙니다"));
        }
    }

    @DeleteMapping("/friends")
    public ResponseEntity<ApiResponse<Void>> removeFriend(
            @AuthenticationPrincipal String uid,
            @RequestParam String friendEmail) {

        try {
            friendService.removeFriend(uid, friendEmail);
            return ResponseEntity.ok(ApiResponse.success(null, "친구가 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("사용자 또는 친구를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("친구 삭제 중 오류가 발생했습니다."));
        }
    }


}
