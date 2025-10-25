package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.addMoimRequestDto;
import com.example.tomo.Moim.dtos.getMoimResponseDTO;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.global.ApiResponse;
import com.example.tomo.global.DuplicatedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Moim API", description = "모임 관련 API")
@RestController
@RequestMapping("/public")
public class MoimController {

    private final MoimService moimService;

    public MoimController(MoimService moimService) {
        this.moimService = moimService;
    }

    @Operation(
            summary = "모임 생성",
            description = "새로운 모임을 생성합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모임 생성 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 사용자"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 모임")
            }
    )
    @PostMapping("/moims")
    public ResponseEntity<ResponsePostUniformDto> addmoim(
            @RequestBody addMoimRequestDto dto) {
        try {
            return ResponseEntity.ok(moimService.addMoim(dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponsePostUniformDto(false ,"존재하지 않은 사용자를 친구로 추가했습니다"));
        } catch (DuplicatedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponsePostUniformDto(false ,"이미 존재하는 모임입니다."));
        }
    }

    @Operation(
            summary = "모임 상세 조회",
            description = "모임 이름으로 모임 상세 정보를 조회합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "모임 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 모임")
            }
    )
    @GetMapping("/moims")
    public ResponseEntity<ApiResponse<getMoimResponseDTO>> moimGet(
            @Parameter(description = "조회할 모임 이름", required = true)
            @RequestParam String title) {
        try {
            return ResponseEntity.ok(ApiResponse.success(moimService.getMoim(title),"성공"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(ApiResponse.failure("존재하지 않는 모임입니다."));
        }
    }

    @Operation(
            summary = "내 모임 리스트 조회",
            description = "로그인한 사용자가 속한 모든 모임 정보를 조회합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내 모임 조회 성공")
            }
    )
    @GetMapping("/moims/list")
    public ResponseEntity<ApiResponse<List<getMoimResponseDTO>>> getAllMoims(
            @Parameter(description = "로그인한 사용자의 UUID", required = true)
            @AuthenticationPrincipal String uid) {
        return ResponseEntity.ok(
                ApiResponse.success(moimService.getMoimList(uid), "모임 조회 성공")
        );
    }
}
