package com.auth.template.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank
    @Email(message = "Invalid email format")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
        message = "Email must be a valid format"
    )
    private String email;

    private String otp;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
        message = "Password must be at least 5 characters long, contain upper and lower case letters, a digit, and a special character"
    )
    private String newPassword;
}
