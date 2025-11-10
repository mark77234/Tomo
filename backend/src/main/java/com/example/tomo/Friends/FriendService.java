package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;
    private final FriendShipPolicy friendShipPolicy;

    // 매일 자정마다 실행
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
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
    public ResponseFriendDetailDto getFriend(String uid, String email){
        Friend friend = this.getFriendByUidAndEmail(uid,email);
        return new ResponseFriendDetailDto(email, friend.getFriendship(),friend.getCreated_at());
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
