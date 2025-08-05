package com.auth.template.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDTO {
    private String userNameOrEmail;
    private String password;
}
