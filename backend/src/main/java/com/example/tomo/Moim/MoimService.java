package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.addMoimRequestDto;
import com.example.tomo.Moim.dtos.getMoimResponseDTO;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
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

    @Transactional // 이메일로 처리하기
    public ResponsePostUniformDto addMoim(String uid, addMoimRequestDto dto) {
        if (moimRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicatedException("이미 존재하는 모임 이름입니다.");
        }

        Moim moim = new Moim(dto.getTitle(), dto.getDescription()); // 일단 생성자도 변경해야 해서 그대로 두기
        moimRepository.save(moim);

        List<String> emailList = dto.getEmails();
        User leader = userRepository.findByFirebaseId(uid).orElseThrow(EntityNotFoundException::new);
        Moim_people moimLeader = new Moim_people(moim, leader, true);
        moimPeopleRepository.save(moimLeader);

        for (String email : emailList) {

            Optional<User> user = userRepository.findByEmail(email);

            if(user.isEmpty()){
                throw new EntityNotFoundException("해당 이메일의 사용자가 존재하지 않습니다");
            }

            Moim_people moim_people = new Moim_people(moim, user.get(), false);
            moimPeopleRepository.save(moim_people);
        }

        return new ResponsePostUniformDto(true ,"모임 생성 완료 ");
    }

    // 모임 단일 조회
    @Transactional
    public getMoimResponseDTO getMoim(String title) {
         Moim moim= moimRepository.findByTitle(title).orElseThrow(
                 () -> new EntityNotFoundException("존재하지 않는 모임입니다")
         );

        // 모임의 리더를 찾아서 이름을 반환해주어야 함
        Long leaderId = moimPeopleRepository.findBymoimLeader(moim.getId());
        String name = userRepository.findById(leaderId).orElseThrow(EntityNotFoundException::new).getUsername();

        return new getMoimResponseDTO(
                moim.getTitle(),
                moim.getDescription(),
                moim.getMoimPeopleList().size(),
                name,
                moim.getCreatedAt());

    }
    // 모임 상세 조회

    @Transactional
    public List<getMoimResponseDTO> getMoimList(String userId){
        // 액세스 토큰이 들어오면 처리할 파트

        User user = userRepository.findByFirebaseId(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 UID를 가진 사용자가 존재하지 않습니다."));

        List<Moim_people> moims = moimPeopleRepository.findByUserId(user.getId());
        List<getMoimResponseDTO> moimResponseDTOList = new ArrayList<>();

        for(Moim_people moim_people : moims){
            moimResponseDTOList.add(this.getMoim(moim_people.getMoim().getTitle()));
        }

        return moimResponseDTOList;
    }

}

