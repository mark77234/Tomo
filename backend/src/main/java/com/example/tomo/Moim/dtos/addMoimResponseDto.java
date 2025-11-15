package com.example.tomo.Moim.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class addMoimResponseDto {
    private Long moim_id;
    private String title;
    private String description;
    private List<String> peopleList;
}
