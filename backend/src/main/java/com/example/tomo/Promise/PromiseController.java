package com.example.tomo.Promise;

import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.global.ApiResponse;
import com.example.tomo.global.DuplicatedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Promise API", description = "약속 관련 API")
@RestController
@RequestMapping("/public")
public class PromiseController {

    private final PromiseService promiseService;

    public PromiseController(PromiseService promiseService) {
        this.promiseService = promiseService;
    }

    @Operation(summary = "약속 추가", description = "새로운 약속을 생성합니다")
    @PostMapping("/promises")
    public ResponseEntity<ResponsePostUniformDto> addPromise(
            @RequestBody addPromiseRequestDTO dto) {
        try {
            return ResponseEntity.ok().body(promiseService.addPromise(dto));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponsePostUniformDto(false, "모임을 먼저 생성해 주세요"));
        } catch (DuplicatedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsePostUniformDto(false, "이미 존재하는 약속입니다"));
        }
    }

    @Operation(summary = "약속 단건 조회", description = "약속 이름으로 특정 약속을 조회합니다")
    @GetMapping("/promises")
    public ResponseEntity<ApiResponse<ResponseGetPromiseDto>> getPromise(
            @Parameter(description = "조회할 약속 이름", required = true)
            @RequestParam String promiseName) {
        try {
            return ResponseEntity.ok(ApiResponse
                    .success(promiseService.getPromise(promiseName), "약속 조회 성공"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("존재하지 않는 약속을 조회했습니다"));
        }
    }

    @Operation(summary = "모임의 모든 약속 조회", description = "모임 이름으로 해당 모임의 모든 약속을 조회합니다")
    @GetMapping("/moims/promises")
    public ResponseEntity<ApiResponse<List<ResponseGetPromiseDto>>> getAllPromises(
            @Parameter(description = "모임 이름", required = true)
            @RequestParam String moimName) {
        try {
            return ResponseEntity.ok(ApiResponse.success(promiseService.getAllPromise(moimName), "성공"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("해당 모임이 존재하지 않습니다"));
        }
    }
}
