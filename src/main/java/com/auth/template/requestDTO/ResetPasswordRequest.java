package com.auth.template.requestDTO;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    private String email;

    private String otp;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
        message = "Password must be at least 5 characters long, contain upper and lower case letters, a digit, and a special character"
    )
    private String newPassword;
}
