package com.example.tomo.Moim;

import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.ResponseUniformDto;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.global.DuplicatedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MoimService {

    private final MoimRepository moimRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;

    @Autowired
    public MoimService(MoimRepository moimRepository, UserRepository userRepository, MoimPeopleRepository moimPeopleRepository) {
        this.moimRepository = moimRepository;
        this.moimPeopleRepository = moimPeopleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseUniformDto addMoim(addMoimRequestDto dto) {
        if (moimRepository.existsByMoimName(dto.getMoimName())) {
            throw new DuplicatedException("이미 존재하는 모임 이름입니다.");
        }

        Moim moim = new Moim(dto.getMoimName(), dto.getDescription());
        moimRepository.save(moim);

        for (String userName : dto.getUserNames()) {

            Optional<User> user = userRepository.findByUsername(userName);

            if(user.isEmpty()){
                throw new EntityNotFoundException("해당 이름의 사용자가 존재하지 않습니다");
            }

            Moim_people moim_people = new Moim_people(moim, user.get());
            moimPeopleRepository.save(moim_people);
        }

        return new ResponseUniformDto(true ,"모임 생성 완료 ");
    }

    // 모임 단일 조회
    @Transactional
    public getMoimResponseDTO getMoim(String moimName) {
         Moim moim= moimRepository.findByMoimName(moimName).orElseThrow(
                 () -> new EntityNotFoundException("존재하지 않는 모임입니다")
         );

        return new getMoimResponseDTO(moim.getMoimName(), moim.getDescription(), moim.getMoimPeopleList().size());
    }
    // 모임 상세 조회

    @Transactional
    public List<getMoimResponseDTO> getMoimList(){
        // 액세스 토큰이 들어오면 처리할 파트
        Long userId = 1L;

        List<Moim_people> moims = moimPeopleRepository.findByUserId(userId);
        List<getMoimResponseDTO> moimResponseDTOList = new ArrayList<>();

        for(Moim_people moim_people : moims){
            moimResponseDTOList.add(this.getMoim(moim_people.getMoim().getMoimName()));
        }

        return moimResponseDTOList;
    }

}

