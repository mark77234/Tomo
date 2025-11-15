package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;
import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.Moim.MoimRepository;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.global.SelfFriendRequestException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final MoimPeopleRepository moimPeopleRepository;
    private final MoimRepository moimRepository;

    // 사용자 존재 시 true 반환 404
    public User userSignUp(User entity) {

        Optional<User> user = userRepository.findByEmail(entity.getEmail());
        if(user.isEmpty()){
            throw new EntityNotFoundException("친구 요청한 사용자가 존재하지 않는 사용자 입니다");
        }
        return user.get();
    }

    // 이미 친구 관계이면 TRUE 404
    public boolean alreadyFriend(User user, User friend){
        // user-friend 관계 존재 여부 체크
        return friendRepository.existsByUserAndFriend(user, friend);

    }

    // 친구 추가하기
    // DTO  변환하기
    @Transactional
    public ResponsePostUniformDto addFriends(String uid, String query) {

        // 친구 요청을 보내는 사용자를 먼저 조회
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다. 회원가입 또는 로그인을 진행해 주세요 "));

        User friend = getUser(query);

        if (user.getEmail().equals(friend.getEmail())) {
            throw new SelfFriendRequestException("자기 자신은 친구로 추가할 수 없습니다.");
        }

        // 이미 친구 관계
        if (alreadyFriend(user, friend) ) {
            throw new EntityExistsException("이미 친구 관계입니다");
        }

        User realFriend =  userSignUp(friend); // 유저의 회원가입 여부를 검증
        Friend friends = new Friend(user, realFriend);
        Friend reverseFriend = new Friend(realFriend, user);

        friendRepository.save(friends);
        friendRepository.save(reverseFriend);
        return new ResponsePostUniformDto(true , "success");

    }

    private User getUser(String query) {
        User email = userRepository.findByEmail(query).orElse(null);
        User invite = userRepository.findByInviteCode(query).orElse(null);

        User existUser;

        if (email != null && invite != null) {
            throw new IllegalArgumentException("email과 inviteCode 중 하나만 전달해야 합니다.");
        }

        if (email != null) {
            existUser = email;
        } else if (invite != null) {
            existUser = invite;
        } else {
            throw new IllegalArgumentException("email 또는 inviteCode 중 하나는 반드시 필요합니다.");
        }
        return existUser;
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

    public getFriendResponseDto getUserInfo(String query){

        User user = getUser(query);
        return new getFriendResponseDto(user.getUsername(), user.getEmail());
    }

    public void saveRefreshToken(String uid, String refreshToken){
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("로그인 되지 않은 사용자입니다."));


        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String uid) {
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        Long userId = user.getId();

        //  리더 모임 ID 조회
        List<Long> leaderMoimIds = moimPeopleRepository.findLeaderMoimIds(userId);

        if (!leaderMoimIds.isEmpty()) {
            // 리더 모임 참여자 삭제
            moimPeopleRepository.deleteMoimPeopleByMoimIds(leaderMoimIds);
            //  리더 모임 삭제
            moimRepository.deleteMoimsByIds(leaderMoimIds);
        }

        // 일반 멤버 모임에서 본인만 삭제
        moimPeopleRepository.deleteUserFromNonLeaderMoims(userId);

        // 친구 관계 삭제 (선택)
        friendRepository.deleteAllByUserId(userId);

        // 사용자 삭제
        userRepository.delete(user);
    }
    public void logout(String uid) {
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
        // Refresh Token 삭제
        user.setRefreshToken(null);
        userRepository.save(user);
    }


}
