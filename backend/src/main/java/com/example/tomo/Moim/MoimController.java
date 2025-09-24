package com.example.tomo.Moim;

import com.example.tomo.Users.ResponseUniformDto;
import com.example.tomo.global.ApiResponse;
import com.example.tomo.global.DuplicatedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class MoimController {

    private final MoimService moimService;


    public MoimController(MoimService moimService) {
        this.moimService = moimService;


    }

    // 모임 생성하기
    @PostMapping("/moims")
    public ResponseEntity<ResponseUniformDto> addmoim(@RequestBody addMoimRequestDto dto) {

       try{
           return ResponseEntity.ok(moimService.addMoim(dto));
       }
       catch(EntityNotFoundException e){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                   new ResponseUniformDto(false ,"존재하지 않은 사용자를 친구로 추가했습니다"));
       }
       catch(DuplicatedException e){
           return ResponseEntity.status(HttpStatus.CONFLICT).body(
                   new ResponseUniformDto(false ,"이미 존재하는 모임입니다."));
       }


    }

    // 모임 상세 조회하기
    @GetMapping("/moims")
    public ResponseEntity<ApiResponse<getMoimResponseDTO>> moimGet(@RequestParam String moimName) {

        try{
            return ResponseEntity.ok(ApiResponse.success(moimService.getMoim(moimName),"성공"));
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(ApiResponse.failure("존재하지 않는 모임입니다."));
        }
    }

    // 사용자 ID에 해당하는 모임 정보 불러오기
    @GetMapping("moims/mine")
    public ResponseEntity<List<getMoimResponseDTO>> getAllMoims() {
        return ResponseEntity.ok().body(moimService.getMoimList());
    }



}
