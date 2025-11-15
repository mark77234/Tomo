package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.addMoimRequestDto;
import com.example.tomo.Moim.dtos.getDetailMoimDto;
import com.example.tomo.Moim.dtos.getMoimResponseDTO;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.dtos.userSimpleDto;
import com.example.tomo.global.DuplicatedException;
import com.example.tomo.global.NotLeaderUserException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoimService {

    private final MoimRepository moimRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;


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
    public getMoimResponseDTO getMoim(String title, String uid) {
         Moim moim= moimRepository.findByTitle(title).orElseThrow(
                 () -> new EntityNotFoundException("존재하지 않는 모임입니다")
         );
        Long id = userRepository.findByFirebaseId(uid).orElseThrow(EntityNotFoundException::new).getId();
        // 모임의 리더 여부 출력
        Boolean moimLeader = moimPeopleRepository.findLeaderByMoimIdAndUserId(moim.getId(),id);

        return new getMoimResponseDTO(
                moim.getTitle(),
                moim.getDescription(),
                moim.getMoimPeopleList().size(),
                moimLeader,
                moim.getCreatedAt());

    }
    // 모임 상세 조회

    @Transactional
    public List<getMoimResponseDTO> getMoimList(String userId){

        User user = userRepository.findByFirebaseId(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 UID를 가진 사용자가 존재하지 않습니다."));

        List<Moim_people> moims = moimPeopleRepository.findByUserId(user.getId());
        List<getMoimResponseDTO> moimResponseDTOList = new ArrayList<>();

        for(Moim_people moim_people : moims){
            moimResponseDTOList.add(this.getMoim(moim_people.getMoim().getTitle(), userId));
        }

        return moimResponseDTOList;
    }

    @Transactional
    public getDetailMoimDto getMoimDetail(String title){
        // 1. 모임명을 입력받아, 모임의 ID 알아내기 없다면 예외
        Moim find = moimRepository.findByTitle(title).orElseThrow(EntityNotFoundException::new);
        // 2. 모임 ID를 통해서 moim_people 테이블에서 모임 참가하는 사용자 ID 추출 없다면, 모임에 2명 이상 포함되어 있지 않습니다
        List<Long> userIdList = moimPeopleRepository.findUserIdsByMoimId(find.getId());
        // 3. .stream().map(entity :: toDto).collect.toList 로 반환하기
        List<userSimpleDto> userSimpleDtoList = new ArrayList<>();

        for (Long userId : userIdList) {
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
            Boolean leader = moimPeopleRepository.findLeaderByMoimIdAndUserId(find.getId(), userId);
            userSimpleDto dto = new userSimpleDto(user.getEmail(),leader);
            userSimpleDtoList.add(dto);
        }

        return new getDetailMoimDto(
                find.getTitle(),
                find.getDescription(),
                userSimpleDtoList,
                find.getCreatedAt()
                );

    }
    @Transactional
    public void deleteMoim(String title, String uid) {
        //1. 사용자가 리더일 때만 모임을 삭제할 수 있다.
        User user = userRepository.findByFirebaseId(uid).orElseThrow(EntityNotFoundException::new);
        Moim moim= moimRepository.findByTitle(title).orElseThrow(EntityNotFoundException::new);
        if(!moimPeopleRepository.findLeaderByMoimIdAndUserId(moim.getId(),user.getId())){
            throw new NotLeaderUserException("모임을 삭제할 수 있는 권한이 없습니다");
        }
        //2. 삭제하려는 모임 가져오기,
        moimRepository.delete(moim);
    }
}

