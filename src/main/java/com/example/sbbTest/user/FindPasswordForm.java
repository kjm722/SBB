package com.example.sbbTest.user;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPasswordForm {

    @NotEmpty(message = "아이디는 필수 항목입니다.")
    private String email;
}
