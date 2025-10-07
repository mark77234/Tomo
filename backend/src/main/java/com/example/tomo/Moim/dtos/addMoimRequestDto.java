package com.example.tomo.Moim.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class addMoimRequestDto {

    private String moimName;
    private String description; // 병찬이가 필요없대
    private List<String> emails; // 사용자 이름 , 이메일로
}
