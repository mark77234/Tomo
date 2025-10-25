package com.example.tomo.Moim.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class getMoimResponseDTO {

    private String title;
    private String description;
    private Integer peopleCount;

    public getMoimResponseDTO(String title, String description, Integer peopleCount) {
        this.title = title;
        this.description = description;
        this.peopleCount = peopleCount;
    }

}
