package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;
import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.global.IdConverter;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service

public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public UserService(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    // 사용자 존재 시 true 반환 404
    public boolean userSignUp(addFriendRequestDto dto){

        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if(user.isEmpty()){
            throw new EntityNotFoundException("친구 요청한 사용자가 존재하지 않는 사용자 입니다");
        }
        return true;
    }

    // 이미 친구 관계이면 TRUE 404
    public boolean alreadyFriend(addFriendRequestDto dto){
        // 친구 추가하고자 하는 사용자 엔티티를 꺼내기
        Optional<User> user = userRepository.findByEmail(dto.getEmail());

        if(user.isEmpty()){
            throw new EntityNotFoundException("친구 관계 확인중에 오류");
        }
        return friendRepository.findFriendsByIdById(user.get().getId());

    }
    // 친구 추가하기
    // DTO  변환하기
    @Transactional
    public String addFriends(addFriendRequestDto dto) {

        // 액세스 토큰으로 사용자 인증하기
        // 현재는 ID 가 1인 유저 꺼내기
        Optional<User> user = userRepository.findById(1L);

        // 이미 친구 관계 이거나, 친구로 등록할 사용자가 존재하지 않는 경우
        if(!userSignUp(dto) || alreadyFriend(dto) ) {
            throw new EntityExistsException("이미 친구 관계이거나 요청하신 사용자가 존재하지 않습니다");
        }

        User friend = userRepository.findByEmail(dto.getEmail()).get(); // 얘는 userSignUp에서 존재 여부를 검증하기 때문에 .get() 처리를 안해도 됨
        Friend friends = new Friend(user.get(), friend);
        Friend reverseFriend = new Friend(friend, user.get());

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
