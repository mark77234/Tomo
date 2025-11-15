package com.example.tomo.Friend;
import com.example.tomo.Friends.Friend;
import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.Friends.FriendService;
import com.example.tomo.Friends.FriendShipPolicy;
import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @InjectMocks
    FriendService friendService;

    @Mock
    UserService userService;

    @Mock
    FriendRepository friendRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MoimPeopleRepository moimPeopleRepository;

    @Mock
    FriendShipPolicy friendShipPolicy;

    User user;
    User friendUser;
    Friend friendship;

    @BeforeEach
    void setUp() {
        user = new User("uid123", "User", "user@test.com");
        friendUser = new User("uid456", "Friend", "friend@test.com");

        friendship = new Friend(user, friendUser);
        friendship.updateFriendship(10);

        // created_at 설정
        ReflectionTestUtils.setField(friendship, "created_at", LocalDate.now());
    }

    @Test
    void updateAllFriendshipScores_shouldUpdateScores() {
        when(friendRepository.findAll()).thenReturn(List.of(friendship));
        when(moimPeopleRepository.countCommonMoims(user.getId(), friendUser.getId())).thenReturn(2L);
        when(friendShipPolicy.calculateTotalScore(any(), anyInt())).thenReturn(50);

        friendService.updateAllFriendshipScores();

        assertThat(friendship.getFriendship()).isEqualTo(50);
        verify(friendRepository).saveAll(List.of(friendship));
    }

    @Test
    void removeFriend_shouldDeleteFriendship() {
        // 실제 유저, 친구 객체
        User user = new User("uid123", "User", "user@test.com");
        User friendUser = new User("uid456", "Friend", "friend@test.com");

        // 본인이 친구로 등록한 레코드
        Friend friendship = new Friend(user, friendUser);

        // 친구가 본인을 친구로 등록한 레코드 (reverse)
        Friend reverseFriendship = new Friend(friendUser, user);

        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friendUser));

        when(friendRepository.findByUserAndFriend(user, friendUser))
                .thenReturn(Optional.of(friendship));
        when(friendRepository.findByUserAndFriend(friendUser, user))
                .thenReturn(Optional.of(reverseFriendship));

        // 메서드 실행
        friendService.removeFriend("uid123", "friend@test.com");

        // delete가 각각 1번씩 호출됐는지 검증
        verify(friendRepository).delete(friendship);
        verify(friendRepository).delete(reverseFriendship);
    }




    @Test
    void getFriends_shouldReturnList() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(friendRepository.findAllByUserId(user.getId())).thenReturn(List.of(friendship));
        when(userRepository.findById(friendUser.getId())).thenReturn(Optional.of(friendUser));

        List<ResponseFriendDetailDto> list = friendService.getFriends("uid123");

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getEmail()).isEqualTo("friend@test.com");
    }

    @Test
    void getFriendByUidAndEmail_shouldReturnFriend() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friendUser));
        when(friendRepository.findByUserIdAndFriendId(user.getId(), friendUser.getId())).thenReturn(Optional.of(friendship));

        Friend result = friendService.getFriendByUidAndEmail("uid123", "friend@test.com");

        assertThat(result).isEqualTo(friendship);
    }

    @Test
    void getFriendByUidAndEmail_shouldThrowIfNotExist() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friendUser));
        when(friendRepository.findByUserIdAndFriendId(user.getId(), friendUser.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> friendService.getFriendByUidAndEmail("uid123", "friend@test.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("친구 관계가 아닙니다.");
    }
    @Test
    void getFriendDetail_shouldReturnResponse() {
        // 로그인 유저 조회
        when(userRepository.findByFirebaseId("uid123"))
                .thenReturn(Optional.of(user));

        // query → userService.getUser()
        when(userService.getUser("friend@test.com"))
                .thenReturn(friendUser);

        // 친구 유저 조회
        when(userRepository.findByEmail("friend@test.com"))
                .thenReturn(Optional.of(friendUser));

        // 친구 관계 조회
        when(friendRepository.findByUserIdAndFriendId(
                user.getId(),
                friendUser.getId()
        )).thenReturn(Optional.of(friendship));

        // 실행
        ResponseFriendDetailDto dto =
                friendService.getFriendDetail("uid123", "friend@test.com");

        // 검증
        assertThat(dto.getEmail()).isEqualTo("friend@test.com");
        assertThat(dto.getUsername()).isEqualTo("Friend");
        assertThat(dto.getFriendship()).isEqualTo(10);
        assertThat(dto.getCreatedAt()).isNotNull();
    }

}

