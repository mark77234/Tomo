package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.addMoimRequestDto;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoimServiceTest {

    @Mock
    MoimRepository moimRepository;

    @Mock
    MoimPeopleRepository moimPeopleRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    MoimService moimService;

    @Test
    @DisplayName("모임 상세 조회 테스트, 모임 참가자의 이메일과 리더여부를 출력한다 ")
    public void getMoimPeople() {
        // given
        // 모임 생성하기
        when(moimRepository.findByTitle("모임"))
                .thenReturn(Optional.of(new Moim("모임", "모임 테스트")));

        when(userRepository.findByFirebaseId("1"))
                .thenReturn(Optional.of(new User("1","audtjd","email1")));

        List<String > emails = new ArrayList<>();
        emails.add("email2");


        addMoimRequestDto dto = new addMoimRequestDto();
        dto.setTitle("모임");
        dto.setDescription("모임 테스트");
        dto.setEmails(emails);

        // when
        moimService.addMoim("1", dto);


        // then
        /*Assertions.assertEquals(moimService.getMoimDetail("모임"),
                new getDetailMoimDto("모임, "모임 테스트","));*/
    }
}
