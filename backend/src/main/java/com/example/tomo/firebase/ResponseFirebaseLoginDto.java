package com.example.tomo.firebase;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseFirebaseLoginDto {
    private String accessToken;
    private String refreshToken;
}
