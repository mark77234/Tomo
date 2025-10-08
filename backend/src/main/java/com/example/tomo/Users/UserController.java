package com.example.tomo.Users;

import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.addFriendRequestDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.firebase.ResponseFirebaseLoginDto;
import com.example.tomo.global.ApiResponse;
import com.example.tomo.global.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
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
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService,AuthService authService) {

        this.userService = userService;
        this.authService = authService;

    }

    @Operation(summary = "친구 추가", description = "이메일을 이용하여 친구를 추가합니다.")
    @PostMapping("/public/friends")
    public ResponseEntity<ResponsePostUniformDto> addFriendsUsingEmail(
            @AuthenticationPrincipal String uid,
            @RequestBody addFriendRequestDto dto) {
       try{
           dto.setUid(uid);
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
    // 본인 계정 삭제
    @DeleteMapping("/public/users")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount(@AuthenticationPrincipal String uid) {
        try {
            userService.deleteUser(uid);
            return ResponseEntity.ok(ApiResponse.success(null, "계정이 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("계정 삭제 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "firebase Token 인증 후 jwt 헤더로 발급", description = "인증된 사용자에게 액세스, 리프레쉬 토큰 발급. (JWT/Firebase 보호 필요)")
    @PostMapping("/api/auth/firebase-login")
    public ResponseEntity<ApiResponse<ResponseFirebaseLoginDto>> login(@AuthenticationPrincipal String uid) {
        try {
            ResponseFirebaseLoginDto tokens = authService.loginWithFirebase(uid);
            return ResponseEntity.ok(ApiResponse.success(tokens, "로그인 성공"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("회원가입 먼저 진행해 주세요"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Internal server error"));
        }
    }

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<ApiResponse<ResponseFirebaseLoginDto>> refreshToken(
            @RequestHeader("Refresh-Token") String refreshTokenHeader) {

        try {
            ResponseFirebaseLoginDto tokens = authService.reissueAccessToken(refreshTokenHeader);
            return ResponseEntity.ok(ApiResponse.success(tokens, "Access token 재발급 성공"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.failure("리프레쉬 토큰이 올바르지 않습니다. 다시 로그인해 주세요"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Internal server error"));
        }
    }


}
