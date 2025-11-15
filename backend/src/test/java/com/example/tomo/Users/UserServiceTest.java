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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock FriendRepository friendRepository;
    @Mock MoimPeopleRepository moimPeopleRepository;
    @Mock MoimRepository moimRepository;

    @InjectMocks UserService userService;

    User user;
    User friend;

    private User invokeGetUser(String query) {
        try {
            var method = UserService.class.getDeclaredMethod("getUser", String.class);
            method.setAccessible(true);
            return (User) method.invoke(userService, query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    void setup() {
        user = new User("uid-1", "tomo", "tomo@test.com");
        friend = new User("uid-2", "john", "john@test.com");
    }
    @Test
    void userSignUp_shouldThrowException_whenUserNotFound() {
        // given
        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.userSignUp(friend))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("존재하지 않는");
    }
    @Test
    void userSignUp_shouldReturnUser_whenExists() {
        // given
        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(friend));

        // when
        User result = userService.userSignUp(friend);

        // then
        assertThat(result).isEqualTo(friend);
    }

    @Test
    void alreadyFriend_shouldReturnTrue_whenExists() {
        // given
        when(friendRepository.existsByUserAndFriend(user, friend))
                .thenReturn(true);

        // when
        boolean result = userService.alreadyFriend(user, friend);

        // then
        assertThat(result).isTrue();
    }
    @Test
    void alreadyFriend_shouldReturnFalse_whenNotExists() {
        // given
        when(friendRepository.existsByUserAndFriend(user, friend))
                .thenReturn(false);

        // when
        boolean result = userService.alreadyFriend(user, friend);

        // then
        assertThat(result).isFalse();
    }
    @Test
    void getUser_shouldReturnUserByEmail() {
        when(userRepository.findByEmail("query@test.com")).thenReturn(Optional.of(user));
        when(userRepository.findByInviteCode("query@test.com")).thenReturn(Optional.empty());

        User result = invokeGetUser("query@test.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUser_shouldReturnUserByInviteCode() {
        when(userRepository.findByEmail("invite123")).thenReturn(Optional.empty());
        when(userRepository.findByInviteCode("invite123")).thenReturn(Optional.of(friend));

        User result = invokeGetUser("invite123");

        assertThat(result).isEqualTo(friend);
    }

    @Test
    void getUser_shouldThrowException_whenBothEmailAndInviteMatch() {
        when(userRepository.findByEmail("duplicate")).thenReturn(Optional.of(user));
        when(userRepository.findByInviteCode("duplicate")).thenReturn(Optional.of(friend));

        assertThatThrownBy(() -> userService.getUser("duplicate"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("하나만 전달해야");
    }


    @Test
    void getUser_shouldThrowException_whenBothAreNull() {
        when(userRepository.findByEmail("none")).thenReturn(Optional.empty());
        when(userRepository.findByInviteCode("none")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser("none"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("반드시 필요");
    }

    @Test
    void addFriends_shouldAddFriendSuccessfully() {
        // 1. 로그인한 사용자 조회
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));

        // 2. 친구 조회 (getUser)
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friend));
        when(userRepository.findByInviteCode("friend@test.com")).thenReturn(Optional.empty());

        // 3. 이미 친구 여부
        when(friendRepository.existsByUserAndFriend(user, friend)).thenReturn(false);

        // 4. 회원가입 여부 검증 (userSignUp)
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.of(friend));

        // 테스트 실행
        ResponsePostUniformDto result = userService.addFriends("uid123", "friend@test.com");

        assertThat(result.isSuccess()).isTrue();
        verify(friendRepository, times(2)).save(any(Friend.class));
    }

    @Test
    void addFriends_shouldThrowSelfFriendRequestException() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.addFriends("uid123", "user@test.com"))
                .isInstanceOf(SelfFriendRequestException.class);
    }
    @Test
    void addFriends_shouldThrowEntityExistsException() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friend));
        when(friendRepository.existsByUserAndFriend(user, friend)).thenReturn(true);

        assertThatThrownBy(() -> userService.addFriends("uid123", "friend@test.com"))
                .isInstanceOf(EntityExistsException.class);
    }
    @Test
    void addFriends_shouldThrowEntityNotFoundException_whenUserSignUpFails() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friend));
        when(friendRepository.existsByUserAndFriend(user, friend)).thenReturn(false);
        when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFriends("uid123", "friend@test.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void addFriends_shouldThrowIllegalArgumentException_whenEmailAndInviteBothExist() {
        User other = new User(3L, "uid789", "other", "other@test.com");
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("dup")).thenReturn(Optional.of(friend));
        when(userRepository.findByInviteCode("dup")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> userService.addFriends("uid123", "dup"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void addFriends_shouldThrowIllegalArgumentException_whenQueryNotFound() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("none")).thenReturn(Optional.empty());
        when(userRepository.findByInviteCode("none")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addFriends("uid123", "none"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void signUser_shouldSaveUser_whenAvailable() {
        RequestUserSignDto dto = new RequestUserSignDto("uuid1", "name", "email@test.com");
        when(userRepository.findByFirebaseId("uuid1")).thenReturn(Optional.empty());

        ResponsePostUniformDto result = userService.signUser(dto);

        assertThat(result.isSuccess()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUser_shouldThrowEntityExistsException_whenAlreadyExists() {
        RequestUserSignDto dto = new RequestUserSignDto("uuid1", "name", "email@test.com");
        when(userRepository.findByFirebaseId("uuid1")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.signUser(dto))
                .isInstanceOf(EntityExistsException.class);
    }
    @Test
    void logout_shouldClearRefreshToken() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));

        userService.logout("uid123");

        assertThat(user.getRefreshToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void logout_shouldThrowEntityNotFoundException_whenUserNotFound() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.logout("uid123"))
                .isInstanceOf(EntityNotFoundException.class);
    }
    @Test
    void deleteUser_shouldDeleteUserAndRelatedData() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));
        when(moimPeopleRepository.findLeaderMoimIds(user.getId())).thenReturn(List.of(10L));

        userService.deleteUser("uid123");

        verify(moimPeopleRepository).deleteMoimPeopleByMoimIds(List.of(10L));
        verify(moimRepository).deleteMoimsByIds(List.of(10L));
        verify(moimPeopleRepository).deleteUserFromNonLeaderMoims(user.getId());
        verify(friendRepository).deleteAllByUserId(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowEntityNotFoundException_whenUserNotFound() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser("uid123"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getUserInfo_shouldReturnDto() {
        // given
        when(userRepository.findByEmail("friend@test.com")).thenReturn(Optional.of(friend));
        when(userRepository.findByInviteCode("friend@test.com")).thenReturn(Optional.empty());

        // when
        getFriendResponseDto result = userService.getUserInfo("friend@test.com");

        // then
        assertThat(result.username()).isEqualTo(friend.getUsername());
        assertThat(result.email()).isEqualTo(friend.getEmail());
    }
    @Test
    void saveRefreshToken_shouldUpdateToken() {
        // given
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(user));

        // when
        userService.saveRefreshToken("uid123", "newToken");

        // then
        assertThat(user.getRefreshToken()).isEqualTo("newToken");
        verify(userRepository).save(user);
    }

    @Test
    void saveRefreshToken_shouldThrowException_whenUserNotFound() {
        // given
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.saveRefreshToken("uid123", "token"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("로그인 되지 않은 사용자입니다.");
    }










}
