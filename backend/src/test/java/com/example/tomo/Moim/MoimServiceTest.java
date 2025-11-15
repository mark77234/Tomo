package com.example.tomo.Moim;
import com.example.tomo.Moim.dtos.*;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;

import com.example.tomo.global.Exception.NotLeaderUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoimServiceTest {

    @InjectMocks
    MoimService moimService;

    @Mock
    MoimRepository moimRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MoimPeopleRepository moimPeopleRepository;

    User leader;
    User participant;
    Moim moim;

    @BeforeEach
    void setUp() {
        leader = new User("uid123", "Leader", "leader@test.com");
        participant = new User("uid456", "Participant", "participant@test.com");

        moim = new Moim("Title", "Description");
        ReflectionTestUtils.setField(moim, "id", 1L);
        ReflectionTestUtils.setField(moim, "createdAt", LocalDate.now());
    }

    // ========================= addMoim =========================
    @Test
    void addMoim_shouldReturnResponseDto() {
        addMoimRequestDto dto = new addMoimRequestDto();
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setEmails(Arrays.asList("participant@test.com"));

        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(leader));
        when(userRepository.findByEmail("participant@test.com")).thenReturn(Optional.of(participant));
        when(moimRepository.save(any(Moim.class))).thenReturn(moim);

        addMoimResponseDto response = moimService.addMoim("uid123", dto);

        assertThat(response.getMoim_id()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getDescription()).isEqualTo("Description");
        assertThat(response.getPeopleList()).contains("Participant");


        verify(moimPeopleRepository, times(2)).save(any(Moim_people.class));
        verify(moimRepository).save(any(Moim.class));
    }

    @Test
    void addMoim_shouldThrowException_whenEmailNotFound() {
        addMoimRequestDto dto = new addMoimRequestDto();
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setEmails(Collections.singletonList("unknown@test.com"));

        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(leader));
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moimService.addMoim("uid123", dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 이메일의 사용자가 존재하지 않습니다");
    }

    // ========================= getMoim =========================
    @Test
    void getMoim_shouldReturnDto() {
        when(moimRepository.findById(1L)).thenReturn(Optional.of(moim));
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(leader));
        when(moimPeopleRepository.findLeaderByMoimIdAndUserId(1L, leader.getId())).thenReturn(true);

        getMoimResponseDto dto = moimService.getMoim(1L, "uid123");

        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getDescription()).isEqualTo("Description");
        assertThat(dto.getLeader()).isTrue();
        assertThat(dto.getPeopleCount()).isEqualTo(0);
    }

    @Test
    void getMoim_shouldThrowException_whenMoimNotFound() {
        when(moimRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moimService.getMoim(1L, "uid123"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("존재하지 않는 모임입니다");
    }

    // ========================= getMoimList =========================
    @Test
    void getMoimList_shouldReturnList() {
        Moim_people mp = new Moim_people(moim, leader, true);
        List<Moim_people> mpList = Collections.singletonList(mp);

        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(leader));
        when(moimPeopleRepository.findByUserId(leader.getId())).thenReturn(mpList);
        when(moimRepository.findById(1L)).thenReturn(Optional.of(moim));
        when(moimPeopleRepository.findLeaderByMoimIdAndUserId(1L, leader.getId())).thenReturn(true);

        List<getMoimResponseDto> result = moimService.getMoimList("uid123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Title");
    }

    @Test
    void getMoimList_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moimService.getMoimList("uid123"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("해당 UID를 가진 사용자가 존재하지 않습니다");
    }

    // ========================= getMoimDetail =========================
    @Test
    void getMoimDetail_shouldReturnDto() {
        when(moimRepository.findById(1L)).thenReturn(Optional.of(moim));
        when(moimPeopleRepository.findUserIdsByMoimId(1L)).thenReturn(Collections.singletonList(leader.getId()));
        when(userRepository.findById(leader.getId())).thenReturn(Optional.of(leader));
        when(moimPeopleRepository.findLeaderByMoimIdAndUserId(1L, leader.getId())).thenReturn(true);

        getDetailMoimDto detail = moimService.getMoimDetail(1L);

        assertThat(detail.getTitle()).isEqualTo("Title");
        assertThat(detail.getMembers()).hasSize(1);
        assertThat(detail.getMembers().get(0).getLeader()).isTrue();
    }

    @Test
    void getMoimDetail_shouldThrowException_whenMoimNotFound() {
        when(moimRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moimService.getMoimDetail(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ========================= deleteMoim =========================
    @Test
    void deleteMoim_shouldDeleteSuccessfully() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(leader));
        when(moimRepository.findById(1L)).thenReturn(Optional.of(moim));
        when(moimPeopleRepository.findLeaderByMoimIdAndUserId(1L, leader.getId())).thenReturn(true);

        moimService.deleteMoim(1L, "uid123");

        verify(moimRepository).delete(moim);
    }

    @Test
    void deleteMoim_shouldThrowException_whenNotLeader() {
        when(userRepository.findByFirebaseId("uid123")).thenReturn(Optional.of(leader));
        when(moimRepository.findById(1L)).thenReturn(Optional.of(moim));
        when(moimPeopleRepository.findLeaderByMoimIdAndUserId(1L, leader.getId())).thenReturn(false);

        assertThatThrownBy(() -> moimService.deleteMoim(1L, "uid123"))
                .isInstanceOf(NotLeaderUserException.class)
                .hasMessageContaining("모임을 삭제할 수 있는 권한이 없습니다");
    }
}
