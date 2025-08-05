package com.auth.template.payload;

import com.auth.template.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;
@Getter
@Setter
public class SignUpDTO {
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 3, message = "Username should have at least 3 character")
    private String username;

    @NotEmpty(message = "Email Should not be empty")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
        message = "Email must be a valid format"
    )
    private String email;

    @NotEmpty(message = "Password should not be empty")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
        message = "Password must be at least 5 characters long, contain upper and lower case letters, a digit, and a special character"
    )
    private String password;

    @NotEmpty(message = "First name should not be empty")
    @Size(min = 3, message = "Name should have at least 3 character")
    private String firstName;
    private String lastName;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;
}
