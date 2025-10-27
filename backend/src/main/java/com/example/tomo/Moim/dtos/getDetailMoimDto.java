package com.example.tomo.Moim.dtos;

import com.example.tomo.Users.User;
import com.example.tomo.Users.dtos.userSimpleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class getDetailMoimDto {
    private String title;
    private String description;
    private List<userSimpleDto> members;

    private LocalDateTime createdAt;
    private String leader;
}

