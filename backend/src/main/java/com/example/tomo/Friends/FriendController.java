package com.example.tomo.Friends;


import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Users.UserService;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.global.ReponseType.ApiResponse;
import com.example.tomo.global.ReponseType.NoDataApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friend API", description = "친구 관련 API")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @Operation(
            summary = "친구 추가",
            description = "이메일을 이용하여 친구를 추가합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "친구 추가 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 친구")
            }
    )
    @PostMapping("/friends")
    public ResponseEntity<ResponsePostUniformDto> addFriendsUsingEmail(
            @AuthenticationPrincipal String uid,
            @RequestParam String query
            ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.addFriends(uid,query));
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsePostUniformDto(false, e.getMessage()));
        }
    }

    @Operation(
            summary = "친구 조회",
            description = "이메일로 친구 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "친구 조회 완료"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "친구를 찾을 수 없음")
            }
    )
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<getFriendResponseDto>> getFriendsUsingEmail(@RequestParam String query) {
        try {
            return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(query), "친구 조회 완료"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(e.getMessage()));
        }
    }
    @GetMapping("/friends/detail")
    public ResponseEntity<ApiResponse<ResponseFriendDetailDto>> getFriendsDetailUsingEmail(
            @AuthenticationPrincipal String uid,
            @RequestParam String query
    ) {
       try{
           return ResponseEntity.status(200)
                   .body(ApiResponse.success(friendService.getFriendDetail(uid,query), "조회 성공"));
       }catch (EntityNotFoundException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(e.getMessage()));
       }
    }


    @Operation(
            summary = "친구 목록 조회",
            description = "사용자의 친구 목록을 반환합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자")
            }
    )
    @GetMapping("/friends/list")
    public ResponseEntity<ApiResponse<List<ResponseFriendDetailDto>>> getFriendDetails(
            @AuthenticationPrincipal String uid) {
        try {
            return ResponseEntity.ok(ApiResponse.success(friendService.getFriends(uid), "성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("로그인된 사용자가 아닙니다"));
        }
    }

    @Operation(
            summary = "친구 삭제",
            description = "본인의 친구를 이메일을 통해 삭제합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "친구 삭제 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 또는 친구를 찾을 수 없음"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
            }
    )
    @DeleteMapping("/friends")
    public ResponseEntity<NoDataApiResponse> removeFriend(
            @AuthenticationPrincipal String uid,
            @io.swagger.v3.oas.annotations.Parameter(description = "삭제할 친구 이메일", required = true)
            @RequestParam String friendEmail) {

        try {
            friendService.removeFriend(uid, friendEmail);
            return ResponseEntity.ok(NoDataApiResponse.success( "친구가 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(NoDataApiResponse.failure("사용자 또는 친구를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(NoDataApiResponse.failure("친구 삭제 중 오류가 발생했습니다."));
        }
    }
}
