package com.example.tomo.Users.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class userSimpleDto {
    private String email;
    private Boolean leader;
}
