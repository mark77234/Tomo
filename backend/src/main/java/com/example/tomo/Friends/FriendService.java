package com.example.tomo.Friends;

import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {


    private FriendRepository friendRepository;
    private UserRepository userRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    // 친구 목록 조회하기
    public List<Long> getFriends(Long userId) {
        return friendRepository.getFriends(userId);
    }

    // 친구 상세 정보 출력하기
    public List<ResponseFriendDetailDto> getDetailFriends(Long userId){
        User user = userRepository.findById(userId)
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

                    System.out.println("friendName = " + friendName);

                    return new ResponseFriendDetailDto(
                            friendName,
                            dto.getFriendship(),
                            dto.getFriendPeriod()
                    );

                })
                .collect(Collectors.toList());


    }
}
