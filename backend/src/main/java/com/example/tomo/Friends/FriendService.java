package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;
    private final FriendShipPolicy friendShipPolicy;
    private final UserService userService;

    // 매일 자정마다 실행
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updateAllFriendshipScores() {
        List<Friend> friends = friendRepository.findAll();

        for (Friend friend : friends) {
            long joinCount = moimPeopleRepository.countCommonMoims(
                    friend.getUser().getId(),
                    friend.getFriend().getId()
            );

            int score = friendShipPolicy.calculateTotalScore(
                    friend.getCreated_at(),
                    (int) joinCount
            );

            friend.updateFriendship(score);
        }

        // 일괄 저장
        friendRepository.saveAll(friends);
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
    @Transactional
    public ResponseFriendDetailDto getFriendDetail(String uid, String query){
        User user = userService.getUser(query);
        Friend friend = this.getFriendByUidAndEmail(uid,user.getEmail());

        return new ResponseFriendDetailDto(
                user.getEmail(),
                user.getUsername(),
                friend.getFriendship(),
                friend.getCreated_at());

    }

    @Transactional
    public List<ResponseFriendDetailDto> getFriends(String uid){
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다"));
        List<Friend> friends = friendRepository.findAllByUserId(user.getId());

        return friends.stream()
                .map((friend) -> new ResponseFriendDetailDto(
                        userRepository.findById(friend.getFriend().getId())
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다"))
                                .getEmail(),
                        userRepository.findById(friend.getFriend().getId())
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다"))
                                .getUsername(),
                        friend.getFriendship(),
                        friend.getCreated_at()
                ))
                .collect(Collectors.toList());
    }



    public Friend getFriendByUidAndEmail(String uid, String email){
        User me = userRepository.findByFirebaseId(uid)
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 사용자 입니다"));
        User other = userRepository.findByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 사용자 입니다"));

        return friendRepository.findByUserIdAndFriendId(me.getId(),other.getId())
                .orElseThrow(()->new EntityNotFoundException("친구 관계가 아닙니다."));
    }



}
