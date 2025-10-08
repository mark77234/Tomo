package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;
import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.addFriendRequestDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
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
    public User userSignUp(addFriendRequestDto dto){

        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if(user.isEmpty()){
            throw new EntityNotFoundException("친구 요청한 사용자가 존재하지 않는 사용자 입니다");
        }
        return user.get();
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
    public ResponsePostUniformDto addFriends(addFriendRequestDto dto) {

        // 액세스 토큰으로 사용자 인증하기
        // 현재는 ID 가 1인 유저 꺼내기
        Optional<User> user = userRepository.findById(1L);
        if(user.isEmpty()){
            throw new EntityNotFoundException("친구 요청은 로그인이 선행되어야 합니다");
        }

        // 이미 친구 관계
        if (alreadyFriend(dto) ) {
            throw new EntityExistsException("이미 친구 관계입니다");
        }

        User friend =  userSignUp(dto);
        Friend friends = new Friend(user.get(), friend);
        Friend reverseFriend = new Friend(friend, user.get());

        friendRepository.save(friends);
        friendRepository.save(reverseFriend);
        return new ResponsePostUniformDto(true , "success");

    }
    /// 여기 부터
    public boolean isUserAvailable(RequestUserSignDto dto) {
        return userRepository.findByFirebaseId(dto.getUuid()).isEmpty();
    }


    public ResponsePostUniformDto signUser(RequestUserSignDto dto){


        if(!isUserAvailable(dto)){
           throw new EntityExistsException("요청이 타당하지 않습니다. 이미 가입된 회원입니다");
        }

        User newUser = new User(dto.getUuid(),dto.getUsername(),dto.getEmail());
        userRepository.save(newUser);

        return new ResponsePostUniformDto(true, "success");


    }
    ///  여기까지 수정이 요구

    public getFriendResponseDto getUserInfo(String email){

        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
             throw new EntityNotFoundException("존재하지 않는 사용자입니다");
        }
        return new getFriendResponseDto(user.get().getUsername(), user.get().getEmail());
    }

    public void saveRefreshToken(String uid, String refreshToken){
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("로그인 되지 않은 사용자입니다."));


        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    public void deleteUser(String uid){
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자 또는 UUID의 형식 오류"));

        // 친구 관계 삭제
        friendRepository.deleteAllByUserOrFriend(user, user);

        userRepository.delete(user);
    }


}
