package com.example.sbbTest.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordModifyForm {

    @NotEmpty(message = "기존 비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "새 비밀번호는 필수 항목입니다.")
    private String modifiedPassword;

    @NotEmpty(message = "새 비밀번호 확인은 필수 항목입니다.")
    private String modifiedPasswordConfirm;
}
