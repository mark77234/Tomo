package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.addMoimRequestDto;
import com.example.tomo.Moim.dtos.addMoimResponseDto;
import com.example.tomo.Moim.dtos.getDetailMoimDto;
import com.example.tomo.Moim.dtos.getMoimResponseDto;
import com.example.tomo.global.ReponseType.ApiResponse;
import com.example.tomo.global.ReponseType.NoDataApiResponse;
import com.example.tomo.global.Exception.NotLeaderUserException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Moim API", description = "모임 관련 API")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class MoimController {

    private final MoimService moimService;


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
    public ResponseEntity<ApiResponse<addMoimResponseDto>> addmoim(
            @RequestBody addMoimRequestDto dto,
            @AuthenticationPrincipal String uid
    ) {
        try {
            return ResponseEntity.status(200)
                    .body(ApiResponse.success(moimService.addMoim(uid, dto),"모임이 생성되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.failure("존재하지 않는 사용자가 모임에 포함되었습니다."));
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
    @GetMapping("/moims/{moim_id}")
    public ResponseEntity<ApiResponse<getDetailMoimDto>> moimGet(
            @PathVariable(name ="moim_id") long moimId
            ) {
        try {
            return ResponseEntity.ok(ApiResponse.success(moimService.getMoimDetail(moimId),"성공"));
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
    public ResponseEntity<ApiResponse<List<getMoimResponseDto>>> getAllMoims(
            @Parameter(description = "로그인한 사용자의 UUID", required = true)
            @AuthenticationPrincipal String uid) {
        try{
            return ResponseEntity.ok(ApiResponse.success(moimService.getMoimList(uid), "모임 조회 성공"));

        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure("로그인 후 진행해주세요."));
        }

    }

    @DeleteMapping("/moims/{moim_id}")
    public ResponseEntity<NoDataApiResponse> deleteMoim(
            @PathVariable("moim_id") long moimId,
            @AuthenticationPrincipal String uid
    ){
        try{
            moimService.deleteMoim(moimId,uid);
            return ResponseEntity.noContent().build();
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NoDataApiResponse.failure("삭제하려는 모임이 존재하지 않습니다"));
        }catch(NotLeaderUserException e){
            return ResponseEntity.status(403).body(NoDataApiResponse.failure("모임 삭제는 리더만 가능합니다"));
        }


    }
}
