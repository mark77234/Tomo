package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;
import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.global.IdConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service

public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public UserService(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }


    // 친구 추가하기
    // DTO  변환하기
    @Transactional
    public String addFriends(addFriendRequestDto dto) {

        String phone = dto.getPhone();
        Long id = dto.getFriendRequestUserId();

        User friend = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("친구 사용자 없음"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        boolean exists = user.getFriends().stream()
                .anyMatch(f -> f.getFriend().getId().equals(friend.getId()));

        if (exists) {
            throw new IllegalStateException("이미 친구로 등록된 사용자입니다.");
        }

        Friend friends = new Friend(user, friend);
        Friend reverseFriend = new Friend(friend, user);

        user.addFriend(friends);
        friendRepository.save(friends);
        friendRepository.save(reverseFriend);
        return friend.getUsername();

    }



    public boolean validateUser(RequestUserSignDto dto) {
        // 중복 사용자 가입 로직이 제대로 동작하지 않음
        if(userRepository.findById(IdConverter.stringToLong(dto.getUuid())).isPresent()){
            return true;

        }
        return false;
    }


    public ResponseSignSuccessDto signUser(RequestUserSignDto dto){

        System.out.println("dto.getUuid() = " + dto.getUuid());
        Long id = IdConverter.stringToLong(dto.getUuid());

        User user = new User(id,dto.getEmail(),dto.getUsername());
        userRepository.save(user);

        return new ResponseSignSuccessDto(true, "success");


    }




}
