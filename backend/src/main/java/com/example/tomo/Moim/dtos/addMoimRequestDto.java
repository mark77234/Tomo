package com.example.tomo.Moim.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class addMoimRequestDto {

    private String title;
    private String description; //
    private List<String> emails; // 사용자 이름 , 이메일로
}
