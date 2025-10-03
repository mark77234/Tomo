package com.example.tomo.Promise;

import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.global.ApiResponse;
import com.example.tomo.global.DuplicatedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class PromiseController {

    private final PromiseService promiseService;

    public PromiseController(PromiseService promiseService) {
        this.promiseService = promiseService;
    }

    @PostMapping("/promises")
    public ResponseEntity<ResponsePostUniformDto> addPromise(@RequestBody addPromiseRequestDTO dto) {
        try{
            return ResponseEntity.ok().body(promiseService.addPromise(dto));}
        catch(EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponsePostUniformDto(false, "모임을 먼저 생성해 주세요"));
            }
        catch(DuplicatedException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponsePostUniformDto(false, "이미 존재하는 약속입니다"));
        }

    }

    @GetMapping("/promises")
    public ResponseEntity<ApiResponse<ResponseGetPromiseDto>> getPromise(@RequestParam String promiseName){
        try{
            return ResponseEntity.ok(ApiResponse
                    .success(promiseService.getPromise(promiseName), "약속 조회 성공"));
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure("존재하지 않는 약속을 조회했습니다"));
        }
    }

    // 모임에 포함된 약속들을 조회
    @GetMapping("/moims/promises")
    public ResponseEntity<ApiResponse<List<ResponseGetPromiseDto>>> getAllPromises(@RequestParam String moimName){
        try{
            return ResponseEntity.ok(ApiResponse.success(promiseService.getAllPromise(moimName), "성공"));
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure("해당 모임이 존재하지 않습니다"));
        }

    }
}
