package com.auth.template.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDTO {
    @NotEmpty(message = "This field is required it can not be empty")
    private String userNameOrEmail;
    @NotEmpty(message = "This field is required it can not be empty")
    private String password;
}
