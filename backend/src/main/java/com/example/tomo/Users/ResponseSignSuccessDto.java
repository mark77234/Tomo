package com.example.tomo.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.el.parser.BooleanNode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseSignSuccessDto {
    private Boolean success;
    private String message;
}
