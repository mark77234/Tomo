package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.FriendCalculatedDto;
import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {


    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }


    // 친구 상세 정보 출력하기
    public List<ResponseFriendDetailDto> getDetailFriends(String userId) {


        User user = userRepository.findByFirebaseId(userId)
                .orElseThrow(()->new IllegalArgumentException("친구 상세 정보 출력 중 사용자 인증이 되지 않았습니다. 로그인 부탁"));

        // 친구 엔티티를 가져와서 점수 계산하는 로직 JPQL 작성하기
        // 친구명 호감도 친구가 된 날
        // 마지막 만남은.. 어케해보기 제일 최근 모임의 약속에서 현재 날짜를 빼기

        List<FriendCalculatedDto> dtos = friendRepository.findFriends(user.getId());

        return dtos.stream()
                .map(dto -> {

                    String friendName = userRepository.findById(dto.getUserId())
                            .map(User::getUsername)
                            .orElse("알수 없음");

                    String email = userRepository.findById(dto.getUserId())
                            .map(User::getEmail)
                            .orElse("알 수 없음");

                    System.out.println("friendName = " + friendName);

                    return new ResponseFriendDetailDto(
                            friendName,
                            email,
                            dto.getFriendship(),
                            dto.getFriendPeriod()
                    );

                })
                .collect(Collectors.toList());


    }


    @Transactional
    public void removeFriend(String uid, String friendEmail) {
        // 본인 User 조회
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 친구 User 조회
        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new EntityNotFoundException("친구를 찾을 수 없습니다."));

        // 본인이 친구로 등록한 레코드 삭제
        friendRepository.findByUserAndFriend(user, friend)
                .ifPresent(friendRepository::delete);

        // 친구가 본인을 친구로 등록한 레코드 삭제
        friendRepository.findByUserAndFriend(friend, user)
                .ifPresent(friendRepository::delete);
    }
}
