package com.example.tomo.Users;

import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.firebase.ResponseFirebaseLoginDto;
import com.example.tomo.global.ApiResponse;
import com.example.tomo.global.AuthService;
import com.example.tomo.global.NoDataApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "User API", description = "사용자 회원가입, 친구 관리, 인증 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;


    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 사용자"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "데이터 유효성 오류")
            }
    )
    @PostMapping("/public/signup")
    public ResponseEntity<ResponsePostUniformDto> signUser(@RequestBody RequestUserSignDto dto) {
        try {
            return ResponseEntity.ok(userService.signUser(dto));
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsePostUniformDto(false, e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ResponsePostUniformDto(false, "같은 이메일로 가입 내역이 존재합니다"));
        }
    }



    @Operation(
            summary = "회원 탈퇴 (계정 삭제)",
            description = "Firebase UID 기반으로 사용자 계정을 삭제합니다. "
                    + "해당 사용자가 리더로 있는 모임은 모임 전체가 삭제되며, "
                    + "일반 멤버로 속한 모임에서는 본인만 탈퇴됩니다. "
                    + "모든 친구 관계도 함께 제거됩니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "계정이 삭제되었습니다."
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 사용자"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "계정 삭제 중 서버 오류"
                    )
            }
    )
    @DeleteMapping("/public/users")
    public ResponseEntity<NoDataApiResponse> deleteMyAccount(@AuthenticationPrincipal String uid) {
        try {
            userService.deleteUser(uid);
            return ResponseEntity.ok(NoDataApiResponse.success("계정이 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(NoDataApiResponse.failure("사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(NoDataApiResponse.failure("계정 삭제 중 오류가 발생했습니다."));
        }
    }

    @Operation(
            summary = "회원 탈퇴 (계정 삭제)",
            description = "Firebase UID 기반으로 사용자 계정을 삭제합니다. "
                    + "해당 사용자가 리더로 있는 모임은 모임 전체가 삭제되며, "
                    + "일반 멤버로 속한 모임에서는 본인만 탈퇴됩니다. "
                    + "모든 친구 관계도 함께 제거됩니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "계정이 삭제되었습니다."
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 사용자"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "계정 삭제 중 서버 오류"
                    )
            }
    )
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

    @Operation(
            summary = "Access 토큰 재발급",
            description = "Refresh Token을 이용하여 Access Token 재발급",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "리프레쉬 토큰 불일치"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
            }
    )
    @PostMapping("/api/auth/refresh")
    public ResponseEntity<ApiResponse<ResponseFirebaseLoginDto>> refreshToken(
            @RequestHeader("Refresh-Token") String refreshTokenHeader) {
        try {
            ResponseFirebaseLoginDto tokens = authService.reissueAccessToken(refreshTokenHeader);
            return ResponseEntity.ok(ApiResponse.success(tokens, "Access token 재발급 성공"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.failure("리프레쉬 토큰이 올바르지 않습니다. 다시 로그인해 주세요"));
        } catch (Exception e) {
            return ResponseEntity.status(419)
                    .body(ApiResponse.failure("Internal server error"));
        }
    }

    @Operation(
            summary = "로그아웃",
            description = "현재 사용자의 Refresh Token을 삭제하여 로그아웃 처리",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
            }
    )
    @DeleteMapping("/public/logout")
    public ResponseEntity<NoDataApiResponse> logout(@AuthenticationPrincipal String uid) {
        try {
            userService.logout(uid);
            return ResponseEntity.ok(NoDataApiResponse.success("로그아웃 완료"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(NoDataApiResponse.failure("사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(NoDataApiResponse.failure("로그아웃 중 오류가 발생했습니다."));
        }
    }
}
