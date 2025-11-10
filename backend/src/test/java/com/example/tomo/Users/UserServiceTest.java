package com.example.tomo.Users;


import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.Users.dtos.addFriendRequestDto;
import com.example.tomo.global.SelfFriendRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("사용자 본인을 친구로 추가하는 상황 방지 ")
    public void selfFriendRequest() {

        //given
        when(userRepository.findByFirebaseId("1"))
                .thenReturn(Optional.of(new User("1", "명성","dreamkms2014")));

        //when
        addFriendRequestDto dto = new addFriendRequestDto();
        dto.setEmail("dreamkms2014");
        dto.setUid("1");

        //then
        Assertions.assertThrows(SelfFriendRequestException.class, () -> {
            userService.addFriends(dto);
        });
    }


}
