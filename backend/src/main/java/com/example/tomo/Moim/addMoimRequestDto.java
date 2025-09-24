package com.example.tomo.Moim;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class addMoimRequestDto {

    private String moimName;
    private String description;
    private List<String> userNames;
}
