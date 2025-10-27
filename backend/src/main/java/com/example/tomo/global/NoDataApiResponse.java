package com.example.tomo.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoDataApiResponse {

    private boolean success;
    private String message;

    public static NoDataApiResponse success(String message) {
        return new NoDataApiResponse(true, message);
    }

    public static NoDataApiResponse failure(String message) {
        return new NoDataApiResponse(false, message);
    }

}
