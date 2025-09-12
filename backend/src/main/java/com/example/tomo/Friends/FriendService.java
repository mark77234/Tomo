package com.example.tomo.Friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {


    private FriendRepository friendRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    // 친구 목록 조회하기
    public List<Long> getFriends(Long userId) {

        return friendRepository.getFriends(userId);
    }
}
