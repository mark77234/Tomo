package com.example.tomo.Users;

import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.addFriendRequestDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "User API", description = "사용자 회원가입, 친구 관리 API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "친구 추가", description = "이메일을 이용하여 친구를 추가합니다.")
    @PostMapping("/public/friends")
    public ResponseEntity<ResponsePostUniformDto> addFriendsUsingEmail(@RequestBody addFriendRequestDto dto) {
       try{
           return ResponseEntity.status(HttpStatus.CREATED).body(userService.addFriends(dto));
       }catch (EntityExistsException e){
           return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponsePostUniformDto(false, e.getMessage()));
       }
    }
    @Operation(summary = "친구 조회", description = "이메일을 이용하여 친구 정보를 조회합니다.")
    @GetMapping("/public/friends") // 이메일로 친구 검색
    public ResponseEntity<ApiResponse<getFriendResponseDto>> getFriendsUsingEmail(@RequestParam String email ) {
        try {
            return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(email),"친구 조회 완료"));
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(e.getMessage()));
        }
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/public/signup")
    public ResponseEntity<ResponsePostUniformDto> signUser(@RequestBody RequestUserSignDto dto) {
        try {
            return ResponseEntity.ok(userService.signUser(dto));
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsePostUniformDto(false, e.getMessage()));
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ResponsePostUniformDto(false,"같은 이메일로 가입 내역이 존재합니다"));
        }

    }

    // 로그인도 만들어야 함
    // 인자로 뭘 받을 지 조금 더 고민해봄
   /* @PostMapping
    public ResponseEntity<ResponseLoginDto> loginUser(@RequestBody RequestLoginDto dto){


    }*/
    @Operation(summary = "firebase Token 인증 후 jwt 헤더로 발급", description = "인증된 사용자에게 액세스, 리프레쉬 토큰 발급. (JWT/Firebase 보호 필요)")
    @GetMapping("/api/protected/user")
    public String getUser(@AuthenticationPrincipal String uid) {
        // SecurityContext에 등록된 Firebase UID 가져오기
        return "Hello, user " + uid;
    }


}
