package com.example.tomo.Moim.dtos;

import com.example.tomo.Users.dtos.userSimpleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class getDetailMoimDto {
    private Long moimId;
    private String title;
    private String description;
    private List<userSimpleDto> members;

    private LocalDate createdAt;

}

