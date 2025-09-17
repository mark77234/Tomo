package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import com.example.tomo.Moim.MoimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromiseService {

    private final PromiseRepository promiseRepository;
    private final MoimRepository moimRepository;

    public PromiseService(PromiseRepository promiseRepository, MoimRepository moimRepository) {
        this.promiseRepository = promiseRepository;
        this.moimRepository = moimRepository;
    }

    // 약속 생성하기
    // 같은 날짜 같은 시간에 약속 존재 시에도 오류 발생
    public Long addPromise(addPromiseRequestDTO dto){

         if(promiseRepository.existsByPromiseName(dto.getPromiseName()) &&
                 promiseRepository.existsByPromiseDateAndPromiseTime(dto.getPromiseDate(),dto.getPromiseTime())) {
             throw new RuntimeException("Promise already exists");
         }

         Promise promise = new Promise(dto.getPromiseName(),dto.getPlace(),
                dto.getPromiseTime(),dto.getPromiseDate());

         Moim moim = moimRepository.findById(dto.getMoimId()).orElseThrow(() -> new RuntimeException("모임 생성 후 약속을 만들어 주세요"));

         promise.setMoimBasedPromise(moim);
         return promiseRepository.save(promise).getId();
    }

    // 약속 단일 조회하기 promise_id
    public ResponseGetPromiseDto getPromise(Long promiseId){
        Promise promise = promiseRepository.findById(promiseId).orElseThrow(() -> new RuntimeException("존재하지 않는 약속입니다"));
        return new ResponseGetPromiseDto(promise.getPromiseName(),promise.getPromiseDate(),
        promise.getPromiseTime(),promise.getPlace());


    }
    public List<ResponseGetPromiseDto> getAllPromise(Long moimId){
        return promiseRepository.findByMoimId(moimId);
    }


    // 약속 몰록 조회하기 moim_id, user_id
}
