package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import com.example.tomo.Moim.MoimRepository;
import com.example.tomo.Users.dtos.ResponseUniformDto;
import com.example.tomo.global.DuplicatedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public ResponseUniformDto addPromise(addPromiseRequestDTO dto){

        Moim moim = moimRepository.findByMoimName(dto.getMoimName())
                .orElseThrow(() -> new EntityNotFoundException("모임 생성 후 약속을 만들어 주세요"));

         if(promiseRepository.existsByPromiseName(dto.getPromiseName()) &&
                 promiseRepository.existsByPromiseDateAndPromiseTime(dto.getPromiseDate(),dto.getPromiseTime())) {
             throw new DuplicatedException("이미 존재하는 약속입니다");
         }

         Promise promise = new Promise(dto.getPromiseName(),dto.getPlace(),
                dto.getPromiseTime(),dto.getPromiseDate());

         promise.setMoimBasedPromise(moim);

         promiseRepository.save(promise);
         return new ResponseUniformDto(true , promise.getPromiseName() + " 약속이 생성되었습니다");
    }

    // 약속 단일 조회하기 promise_name
    public ResponseGetPromiseDto getPromise(String promiseName){
        Promise promise = promiseRepository.findByPromiseName(promiseName)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 약속입니다"));

        return new ResponseGetPromiseDto(promise.getPromiseName(),promise.getPromiseDate(),
        promise.getPromiseTime(),promise.getPlace());


    }
    public List<ResponseGetPromiseDto> getAllPromise(String moimName){
        Optional<Moim> moim = moimRepository.findByMoimName(moimName);
        if(moim.isPresent()){
            return promiseRepository.findByMoimId(moim.get().getId());
        }
        else{
            throw new EntityNotFoundException("모임에 약속이 존재하지 않습니다");
        }
    }


    // 약속 몰록 조회하기 moim_id, user_id
}
