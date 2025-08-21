package com.auth.template.requestDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SigninRequest {
    @NotEmpty(message = "This field is required it can not be empty")
    private String userNameOrEmail;
    @NotEmpty(message = "This field is required it can not be empty")
    private String password;
}
